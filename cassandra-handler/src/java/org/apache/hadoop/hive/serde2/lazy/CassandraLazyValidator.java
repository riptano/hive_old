package org.apache.hadoop.hive.serde2.lazy;

import java.nio.ByteBuffer;

import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.CassandraValidatorObjectInspector;
import org.apache.hadoop.io.Text;

public class CassandraLazyValidator  extends
    LazyPrimitive<CassandraValidatorObjectInspector, Text> {
  private final AbstractType validator;

  public CassandraLazyValidator(CassandraValidatorObjectInspector oi) {
    super(oi);
    validator = oi.getValidatorType();
    data = new Text();
  }

  public CassandraLazyValidator(CassandraLazyValidator copy) {
    super(copy.getInspector());
    validator = copy.validator;
    isNull = copy.isNull;
  }

  @Override
  public void init(ByteArrayRef bytes, int start, int length) {

    if ( length == 8 ) {
      try {
        ByteBuffer buf = ByteBuffer.wrap(bytes.getData(), start, length);
        data.set(validator.getString(buf));
        isNull = false;
        return;
      } catch (IndexOutOfBoundsException ie) {
        //we are unable to parse the data, try to parse it in the hive lazy way.
      }
    }

    data.set(bytes.getData(), start, length);
    isNull = true;
  }

}
