package org.apache.hadoop.hive.cassandra;

import java.io.IOException;
import java.nio.ByteBuffer;
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
import org.apache.cassandra.utils.ByteBufferUtil;
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
import org.apache.hadoop.hive.serde2.lazy.LazyCassandraUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraPushdownPredicate {

  private static final Logger logger = LoggerFactory.getLogger(CassandraPushdownPredicate.class);

  /**
   * Get the names of the columns for which there are secondary indexes
   * @param host
   * @param port
   * @param ksName keyspace name
   * @param cfName column family name
   * @return names of the columns which have been indexed. May be empty, but not null
   * @throws CassandraException if a problem is encountered communicating with Cassandra
   */
  public static Set<String> getIndexedColumnNames(String host, int port, String ksName, String cfName) throws CassandraException
  {
    final CassandraProxyClient client = new CassandraProxyClient(host, port, true, true);
    Set<String> indexColumns = new HashSet<String>();
    try {
      KsDef ks = client.getProxyConnection().describe_keyspace(ksName);
      List<CfDef> cfs = ks.getCf_defs();
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
   * Given a set of indexed column names, return an IndexPredicateAnalyzer
   *
   * @param indexedColumns names of indexed columns
   * @return IndexPredicateAnalyzer
   */
  public static IndexPredicateAnalyzer newIndexPredicateAnalyzer(Set<String> indexedColumns) {
    IndexPredicateAnalyzer analyzer = new IndexPredicateAnalyzer();

    // we only support C*'s set of comparisons = > >= =< <
    analyzer.addComparisonOp(GenericUDFOPEqual.class.getName());
    analyzer.addComparisonOp(GenericUDFOPEqualOrGreaterThan.class.getName());
    analyzer.addComparisonOp(GenericUDFOPGreaterThan.class.getName());
    analyzer.addComparisonOp(GenericUDFOPEqualOrLessThan.class.getName());
    analyzer.addComparisonOp(GenericUDFOPLessThan.class.getName());

    for (String columnName : indexedColumns) {
      analyzer.allowColumnName(columnName);
    }

    return analyzer;
  }

  /**
   * An IndexClause in C* must always include at least 1 EQ condition.
   * Validate this constraint is satisified by the list of IndexSearchConditions
   *
   * @return true if there is an EQ operator present, otherwise false
   */
  public static boolean verifySearchConditions(List<IndexSearchCondition> conditions) {
    for (IndexSearchCondition thisCon : conditions) {
      if (thisCon.getComparisonOp().equals(GenericUDFOPEqual.class.getName())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Translate the list of SearchConditions into C* IndexClause. The returned
   * clause object will initially have its start key set to an empty byte[]
   *
   * @param conditions a list of index search condition
   */
  public static IndexClause translateSearchConditions(List<IndexSearchCondition> conditions) throws IOException {
    IndexClause clause = new IndexClause();
    clause.setStart_key("".getBytes());

    List<IndexExpression> exps = new ArrayList<IndexExpression>();
    for (IndexSearchCondition thisCond : conditions) {
      exps.add(translateSearchCondition(thisCond));
    }

    clause.setExpressions(exps);
    return clause;
  }

  private static IndexExpression translateSearchCondition(IndexSearchCondition condition) throws IOException {
    IndexExpression expr = new IndexExpression();

    expr.setColumn_name(condition.getColumnDesc().getColumn().getBytes());
    expr.setOp(getIndexOperator(condition.getComparisonOp()));

    ExprNodeConstantEvaluator eval = new ExprNodeConstantEvaluator(condition.getConstantDesc());
    byte [] value;
    try {
      ObjectInspector objInspector = eval.initialize(null);
      Object writable = eval.evaluate(null);
      ByteStream.Output serializeStream = new ByteStream.Output();
      PrimitiveObjectInspector poi = (PrimitiveObjectInspector) objInspector;
      ByteBuffer bytes = LazyCassandraUtils.getCassandraType(poi).decompose(poi.getPrimitiveJavaObject(writable));
      serializeStream.write(ByteBufferUtil.getArray(bytes));
      value = new byte[serializeStream.getCount()];
      System.arraycopy( serializeStream.getData(), 0, value, 0, serializeStream.getCount());
    } catch (HiveException e) {
      throw new IOException(e);
    }
    expr.setValue(value);
    return expr;
  }

  private static IndexOperator getIndexOperator(String str) throws IOException {
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
