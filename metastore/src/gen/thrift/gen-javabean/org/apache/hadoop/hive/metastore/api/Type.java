/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.apache.hadoop.hive.metastore.api;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Type implements org.apache.thrift.TBase<Type, Type._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Type");

  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField TYPE1_FIELD_DESC = new org.apache.thrift.protocol.TField("type1", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField TYPE2_FIELD_DESC = new org.apache.thrift.protocol.TField("type2", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField FIELDS_FIELD_DESC = new org.apache.thrift.protocol.TField("fields", org.apache.thrift.protocol.TType.LIST, (short)4);

  private String name;
  private String type1;
  private String type2;
  private List<FieldSchema> fields;

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NAME((short)1, "name"),
    TYPE1((short)2, "type1"),
    TYPE2((short)3, "type2"),
    FIELDS((short)4, "fields");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // NAME
          return NAME;
        case 2: // TYPE1
          return TYPE1;
        case 3: // TYPE2
          return TYPE2;
        case 4: // FIELDS
          return FIELDS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TYPE1, new org.apache.thrift.meta_data.FieldMetaData("type1", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TYPE2, new org.apache.thrift.meta_data.FieldMetaData("type2", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.FIELDS, new org.apache.thrift.meta_data.FieldMetaData("fields", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, FieldSchema.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Type.class, metaDataMap);
  }

  public Type() {
  }

  public Type(
    String name)
  {
    this();
    this.name = name;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Type(Type other) {
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetType1()) {
      this.type1 = other.type1;
    }
    if (other.isSetType2()) {
      this.type2 = other.type2;
    }
    if (other.isSetFields()) {
      List<FieldSchema> __this__fields = new ArrayList<FieldSchema>();
      for (FieldSchema other_element : other.fields) {
        __this__fields.add(new FieldSchema(other_element));
      }
      this.fields = __this__fields;
    }
  }

  public Type deepCopy() {
    return new Type(this);
  }

  @Override
  public void clear() {
    this.name = null;
    this.type1 = null;
    this.type2 = null;
    this.fields = null;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public String getType1() {
    return this.type1;
  }

  public void setType1(String type1) {
    this.type1 = type1;
  }

  public void unsetType1() {
    this.type1 = null;
  }

  /** Returns true if field type1 is set (has been assigned a value) and false otherwise */
  public boolean isSetType1() {
    return this.type1 != null;
  }

  public void setType1IsSet(boolean value) {
    if (!value) {
      this.type1 = null;
    }
  }

  public String getType2() {
    return this.type2;
  }

  public void setType2(String type2) {
    this.type2 = type2;
  }

  public void unsetType2() {
    this.type2 = null;
  }

  /** Returns true if field type2 is set (has been assigned a value) and false otherwise */
  public boolean isSetType2() {
    return this.type2 != null;
  }

  public void setType2IsSet(boolean value) {
    if (!value) {
      this.type2 = null;
    }
  }

  public int getFieldsSize() {
    return (this.fields == null) ? 0 : this.fields.size();
  }

  public java.util.Iterator<FieldSchema> getFieldsIterator() {
    return (this.fields == null) ? null : this.fields.iterator();
  }

  public void addToFields(FieldSchema elem) {
    if (this.fields == null) {
      this.fields = new ArrayList<FieldSchema>();
    }
    this.fields.add(elem);
  }

  public List<FieldSchema> getFields() {
    return this.fields;
  }

  public void setFields(List<FieldSchema> fields) {
    this.fields = fields;
  }

  public void unsetFields() {
    this.fields = null;
  }

  /** Returns true if field fields is set (has been assigned a value) and false otherwise */
  public boolean isSetFields() {
    return this.fields != null;
  }

  public void setFieldsIsSet(boolean value) {
    if (!value) {
      this.fields = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case TYPE1:
      if (value == null) {
        unsetType1();
      } else {
        setType1((String)value);
      }
      break;

    case TYPE2:
      if (value == null) {
        unsetType2();
      } else {
        setType2((String)value);
      }
      break;

    case FIELDS:
      if (value == null) {
        unsetFields();
      } else {
        setFields((List<FieldSchema>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NAME:
      return getName();

    case TYPE1:
      return getType1();

    case TYPE2:
      return getType2();

    case FIELDS:
      return getFields();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NAME:
      return isSetName();
    case TYPE1:
      return isSetType1();
    case TYPE2:
      return isSetType2();
    case FIELDS:
      return isSetFields();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Type)
      return this.equals((Type)that);
    return false;
  }

  public boolean equals(Type that) {
    if (that == null)
      return false;

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_type1 = true && this.isSetType1();
    boolean that_present_type1 = true && that.isSetType1();
    if (this_present_type1 || that_present_type1) {
      if (!(this_present_type1 && that_present_type1))
        return false;
      if (!this.type1.equals(that.type1))
        return false;
    }

    boolean this_present_type2 = true && this.isSetType2();
    boolean that_present_type2 = true && that.isSetType2();
    if (this_present_type2 || that_present_type2) {
      if (!(this_present_type2 && that_present_type2))
        return false;
      if (!this.type2.equals(that.type2))
        return false;
    }

    boolean this_present_fields = true && this.isSetFields();
    boolean that_present_fields = true && that.isSetFields();
    if (this_present_fields || that_present_fields) {
      if (!(this_present_fields && that_present_fields))
        return false;
      if (!this.fields.equals(that.fields))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(Type other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Type typedOther = (Type)other;

    lastComparison = Boolean.valueOf(isSetName()).compareTo(typedOther.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, typedOther.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType1()).compareTo(typedOther.isSetType1());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType1()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type1, typedOther.type1);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType2()).compareTo(typedOther.isSetType2());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType2()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type2, typedOther.type2);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFields()).compareTo(typedOther.isSetFields());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFields()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.fields, typedOther.fields);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    org.apache.thrift.protocol.TField field;
    iprot.readStructBegin();
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == org.apache.thrift.protocol.TType.STOP) { 
        break;
      }
      switch (field.id) {
        case 1: // NAME
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.name = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // TYPE1
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.type1 = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // TYPE2
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.type2 = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // FIELDS
          if (field.type == org.apache.thrift.protocol.TType.LIST) {
            {
              org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
              this.fields = new ArrayList<FieldSchema>(_list0.size);
              for (int _i1 = 0; _i1 < _list0.size; ++_i1)
              {
                FieldSchema _elem2;
                _elem2 = new FieldSchema();
                _elem2.read(iprot);
                this.fields.add(_elem2);
              }
              iprot.readListEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        default:
          org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.name != null) {
      oprot.writeFieldBegin(NAME_FIELD_DESC);
      oprot.writeString(this.name);
      oprot.writeFieldEnd();
    }
    if (this.type1 != null) {
      if (isSetType1()) {
        oprot.writeFieldBegin(TYPE1_FIELD_DESC);
        oprot.writeString(this.type1);
        oprot.writeFieldEnd();
      }
    }
    if (this.type2 != null) {
      if (isSetType2()) {
        oprot.writeFieldBegin(TYPE2_FIELD_DESC);
        oprot.writeString(this.type2);
        oprot.writeFieldEnd();
      }
    }
    if (this.fields != null) {
      if (isSetFields()) {
        oprot.writeFieldBegin(FIELDS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, this.fields.size()));
          for (FieldSchema _iter3 : this.fields)
          {
            _iter3.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Type(");
    boolean first = true;

    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (isSetType1()) {
      if (!first) sb.append(", ");
      sb.append("type1:");
      if (this.type1 == null) {
        sb.append("null");
      } else {
        sb.append(this.type1);
      }
      first = false;
    }
    if (isSetType2()) {
      if (!first) sb.append(", ");
      sb.append("type2:");
      if (this.type2 == null) {
        sb.append("null");
      } else {
        sb.append(this.type2);
      }
      first = false;
    }
    if (isSetFields()) {
      if (!first) sb.append(", ");
      sb.append("fields:");
      if (this.fields == null) {
        sb.append("null");
      } else {
        sb.append(this.fields);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
  }

}

