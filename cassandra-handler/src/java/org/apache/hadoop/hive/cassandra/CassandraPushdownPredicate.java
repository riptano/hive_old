package org.apache.hadoop.hive.cassandra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.ColumnDef;
import org.apache.cassandra.thrift.IndexClause;
import org.apache.cassandra.thrift.IndexExpression;
import org.apache.cassandra.thrift.IndexOperator;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.hadoop.hive.cassandra.serde.StandardColumnSerDe;
import org.apache.hadoop.hive.ql.exec.ExprNodeConstantEvaluator;
import org.apache.hadoop.hive.ql.index.IndexPredicateAnalyzer;
import org.apache.hadoop.hive.ql.index.IndexSearchCondition;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPEqual;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPEqualOrGreaterThan;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPEqualOrLessThan;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPGreaterThan;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPLessThan;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.mapred.JobConf;
import org.apache.thrift.TException;

public class CassandraPushdownPredicate {
  //cassandra host name
  private final String host;

  //cassandra host port
  private final int port;

  //cassandra key space name
  private final String ksName;

  //cassandra column family
  private final String cfName;

  public CassandraPushdownPredicate(JobConf conf) {
    //TODO: There is a bug in Hive, table properties and serde properties are not all being set in the configuraiton.
    //We need to look into it and make sure that they are set properly.
    host = conf.get(StandardColumnSerDe.CASSANDRA_HOST, StandardColumnSerDe.DEFAULT_CASSANDRA_HOST);
    port = conf.getInt(StandardColumnSerDe.CASSANDRA_PORT,
        StandardColumnSerDe.DEFAULT_CASSANDRA_PORT);
    ksName = conf.get(StandardColumnSerDe.CASSANDRA_KEYSPACE_NAME);
    cfName = conf.get(StandardColumnSerDe.CASSANDRA_CF_NAME);
    validateConfiguration();
  }

  private void validateConfiguration() {
    if ((host == null || host.equals("")) ||
        (port == 0)) {
        throw new UnsupportedOperationException("you must set the cassandra host and port in your job configuration");
    }

    if ((ksName == null || ksName.equals("")) ||
        (cfName == null || cfName.equals(""))) {
        throw new UnsupportedOperationException("you must set the keyspace and columnfamily in your job configuration");
    }
  }

  /**
   * Get the names of the columns which have been indexed in cassandra.
   *
   * @param conf job configuration
   * @return a set of cassandra column names which have been indexed; empty set if no column has been indexed.
   *
   * @throws CassandraException when not able to get the column names from cassandra
   */
  public Set<String> getIndexColumnNames() throws CassandraException
  {
    final CassandraProxyClient client = new CassandraProxyClient(host, port, true, true);

    Set<String> indexColumns = new HashSet<String>();

    try {
      KsDef ks = client.getProxyConnection().describe_keyspace(ksName);
      List<CfDef> cfs = ks.getCf_defs();
      //Get the column family definition.
      CfDef cfDef = null;
      for (CfDef thisCf : cfs) {
        if (thisCf.getName().equalsIgnoreCase(cfName)) {
          cfDef = thisCf;
          break;
        }
      }

      //Retrieve a list of indexed column names.
      List<ColumnDef> columns = cfDef.getColumn_metadata();
      for (ColumnDef thisColumn : columns) {
        if (thisColumn.isSetIndex_type()) {
          //this column is indexed
          indexColumns.add(new String(thisColumn.getName()));
        }
      }
    } catch (TException e) {
      throw new CassandraException(e);
    } catch (InvalidRequestException e) {
      throw new CassandraException(e);
    } catch (NotFoundException e) {
      throw new CassandraException(e);
    }

    return indexColumns;
  }


  /**
   * Return an IndexPredicateAnalyzer from the indexed column names.
   *
   * @param columnNames indexed column names
   * @return IndexPredicateAnalyzer
   */
  public IndexPredicateAnalyzer newIndexPredicateAnalyzer(Set<String> columnNames) {
    IndexPredicateAnalyzer analyzer = new IndexPredicateAnalyzer();

    // for now, we only support = > >+ < <+ comparisons
    analyzer.addComparisonOp(GenericUDFOPEqual.class.getName());
    analyzer.addComparisonOp(GenericUDFOPEqualOrGreaterThan.class.getName());
    analyzer.addComparisonOp(GenericUDFOPGreaterThan.class.getName());
    analyzer.addComparisonOp(GenericUDFOPEqualOrLessThan.class.getName());
    analyzer.addComparisonOp(GenericUDFOPLessThan.class.getName());

    // and only on the key column
    analyzer.clearAllowedColumnNames();
    for (String columnName : columnNames) {
      analyzer.allowColumnName(columnName);
    }

    return analyzer;
  }

  /**
   * Return true if there is at lease one search condition that is EQ. Otherwise return false;
   *
   * @return false if there is no EQ operator, otherwise, false;
   */
  public boolean verify(List<IndexSearchCondition> conditions) {
    for (IndexSearchCondition thisCon : conditions) {
      if (thisCon.getComparisonOp().equals(GenericUDFOPEqual.class.getName())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Translate the search condition into IndexClause.
   *
   * @param conditions a list of index search condition
   */
  public IndexClause translateSearchConditions(List<IndexSearchCondition> conditions)
    throws IOException {
    IndexClause clause = new IndexClause();
    clause.setStart_key("".getBytes());

    List<IndexExpression> exps = new ArrayList<IndexExpression>();
    for (IndexSearchCondition thisCond : conditions) {
      exps.add(translateSearchCondition(thisCond));
    }

    clause.setExpressions(exps);

    return clause;
  }

  private IndexExpression translateSearchCondition(IndexSearchCondition condition)
    throws IOException {
    IndexExpression expr = new IndexExpression();

    expr.setColumn_name(condition.getColumnDesc().getColumn().getBytes());
    expr.setOp(getIndexOperator(condition.getComparisonOp()));

    ExprNodeConstantEvaluator eval =
      new ExprNodeConstantEvaluator(condition.getConstantDesc());
    byte [] value;
    try {
      ObjectInspector objInspector = eval.initialize(null);
      Object writable = eval.evaluate(null);
      ByteStream.Output serializeStream = new ByteStream.Output();
      LazyUtils.writePrimitiveUTF8(
        serializeStream,
        writable,
        (PrimitiveObjectInspector) objInspector,
        false,
        (byte) 0,
        null);
      value = new byte[serializeStream.getCount()];
      System.arraycopy(
        serializeStream.getData(), 0,
        value, 0, serializeStream.getCount());
    } catch (HiveException e) {
      throw new IOException(e);
    }
    expr.setValue(value);

    return expr;
  }

  /**
   * Translate the operator in ql query format into index operator that cassandra recognizes.
   *
   * @param str operator in ql query format
   * @return cassandra index operator
   */
  private IndexOperator getIndexOperator(String str) throws IOException {
    if (str.equals(GenericUDFOPEqual.class.getName())) {
      return IndexOperator.EQ;
    } else if(str.equals(GenericUDFOPEqualOrGreaterThan.class.getName())) {
      return IndexOperator.GTE;
    } else if(str.equals(GenericUDFOPGreaterThan.class.getName())) {
      return IndexOperator.GT;
    } else if(str.equals(GenericUDFOPEqualOrLessThan.class.getName())) {
      return IndexOperator.LTE;
    } else if(str.equals(GenericUDFOPLessThan.class.getName())) {
      return IndexOperator.LT;
    } else {
      throw new IOException("Unable to get index operator matches " + str);
    }
  }
}
