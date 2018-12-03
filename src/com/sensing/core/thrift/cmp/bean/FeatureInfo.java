/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.sensing.core.thrift.cmp.bean;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "all"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2018-07-03")
public class FeatureInfo implements org.apache.thrift.TBase<FeatureInfo, FeatureInfo._Fields>, java.io.Serializable, Cloneable, Comparable<FeatureInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FeatureInfo");

  private static final org.apache.thrift.protocol.TField FEATUREID_FIELD_DESC = new org.apache.thrift.protocol.TField("featureid", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField FETURE_FIELD_DESC = new org.apache.thrift.protocol.TField("feture", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new FeatureInfoStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new FeatureInfoTupleSchemeFactory();

  public java.lang.String featureid; // required
  public java.nio.ByteBuffer feture; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    FEATUREID((short)1, "featureid"),
    FETURE((short)2, "feture");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // FEATUREID
          return FEATUREID;
        case 2: // FETURE
          return FETURE;
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
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.FEATUREID, new org.apache.thrift.meta_data.FieldMetaData("featureid", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.FETURE, new org.apache.thrift.meta_data.FieldMetaData("feture", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FeatureInfo.class, metaDataMap);
  }

  public FeatureInfo() {
  }

  public FeatureInfo(
    java.lang.String featureid,
    java.nio.ByteBuffer feture)
  {
    this();
    this.featureid = featureid;
    this.feture = org.apache.thrift.TBaseHelper.copyBinary(feture);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FeatureInfo(FeatureInfo other) {
    if (other.isSetFeatureid()) {
      this.featureid = other.featureid;
    }
    if (other.isSetFeture()) {
      this.feture = org.apache.thrift.TBaseHelper.copyBinary(other.feture);
    }
  }

  public FeatureInfo deepCopy() {
    return new FeatureInfo(this);
  }

  @Override
  public void clear() {
    this.featureid = null;
    this.feture = null;
  }

  public java.lang.String getFeatureid() {
    return this.featureid;
  }

  public FeatureInfo setFeatureid(java.lang.String featureid) {
    this.featureid = featureid;
    return this;
  }

  public void unsetFeatureid() {
    this.featureid = null;
  }

  /** Returns true if field featureid is set (has been assigned a value) and false otherwise */
  public boolean isSetFeatureid() {
    return this.featureid != null;
  }

  public void setFeatureidIsSet(boolean value) {
    if (!value) {
      this.featureid = null;
    }
  }

  public byte[] getFeture() {
    setFeture(org.apache.thrift.TBaseHelper.rightSize(feture));
    return feture == null ? null : feture.array();
  }

  public java.nio.ByteBuffer bufferForFeture() {
    return org.apache.thrift.TBaseHelper.copyBinary(feture);
  }

  public FeatureInfo setFeture(byte[] feture) {
    this.feture = feture == null ? (java.nio.ByteBuffer)null : java.nio.ByteBuffer.wrap(feture.clone());
    return this;
  }

  public FeatureInfo setFeture(java.nio.ByteBuffer feture) {
    this.feture = org.apache.thrift.TBaseHelper.copyBinary(feture);
    return this;
  }

  public void unsetFeture() {
    this.feture = null;
  }

  /** Returns true if field feture is set (has been assigned a value) and false otherwise */
  public boolean isSetFeture() {
    return this.feture != null;
  }

  public void setFetureIsSet(boolean value) {
    if (!value) {
      this.feture = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case FEATUREID:
      if (value == null) {
        unsetFeatureid();
      } else {
        setFeatureid((java.lang.String)value);
      }
      break;

    case FETURE:
      if (value == null) {
        unsetFeture();
      } else {
        if (value instanceof byte[]) {
          setFeture((byte[])value);
        } else {
          setFeture((java.nio.ByteBuffer)value);
        }
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case FEATUREID:
      return getFeatureid();

    case FETURE:
      return getFeture();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case FEATUREID:
      return isSetFeatureid();
    case FETURE:
      return isSetFeture();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof FeatureInfo)
      return this.equals((FeatureInfo)that);
    return false;
  }

  public boolean equals(FeatureInfo that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_featureid = true && this.isSetFeatureid();
    boolean that_present_featureid = true && that.isSetFeatureid();
    if (this_present_featureid || that_present_featureid) {
      if (!(this_present_featureid && that_present_featureid))
        return false;
      if (!this.featureid.equals(that.featureid))
        return false;
    }

    boolean this_present_feture = true && this.isSetFeture();
    boolean that_present_feture = true && that.isSetFeture();
    if (this_present_feture || that_present_feture) {
      if (!(this_present_feture && that_present_feture))
        return false;
      if (!this.feture.equals(that.feture))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetFeatureid()) ? 131071 : 524287);
    if (isSetFeatureid())
      hashCode = hashCode * 8191 + featureid.hashCode();

    hashCode = hashCode * 8191 + ((isSetFeture()) ? 131071 : 524287);
    if (isSetFeture())
      hashCode = hashCode * 8191 + feture.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(FeatureInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetFeatureid()).compareTo(other.isSetFeatureid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFeatureid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.featureid, other.featureid);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetFeture()).compareTo(other.isSetFeture());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFeture()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.feture, other.feture);
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
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("FeatureInfo(");
    boolean first = true;

    sb.append("featureid:");
    if (this.featureid == null) {
      sb.append("null");
    } else {
      sb.append(this.featureid);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("feture:");
    if (this.feture == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.feture, sb);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FeatureInfoStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FeatureInfoStandardScheme getScheme() {
      return new FeatureInfoStandardScheme();
    }
  }

  private static class FeatureInfoStandardScheme extends org.apache.thrift.scheme.StandardScheme<FeatureInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, FeatureInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // FEATUREID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.featureid = iprot.readString();
              struct.setFeatureidIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // FETURE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.feture = iprot.readBinary();
              struct.setFetureIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, FeatureInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.featureid != null) {
        oprot.writeFieldBegin(FEATUREID_FIELD_DESC);
        oprot.writeString(struct.featureid);
        oprot.writeFieldEnd();
      }
      if (struct.feture != null) {
        oprot.writeFieldBegin(FETURE_FIELD_DESC);
        oprot.writeBinary(struct.feture);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FeatureInfoTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public FeatureInfoTupleScheme getScheme() {
      return new FeatureInfoTupleScheme();
    }
  }

  private static class FeatureInfoTupleScheme extends org.apache.thrift.scheme.TupleScheme<FeatureInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, FeatureInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetFeatureid()) {
        optionals.set(0);
      }
      if (struct.isSetFeture()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetFeatureid()) {
        oprot.writeString(struct.featureid);
      }
      if (struct.isSetFeture()) {
        oprot.writeBinary(struct.feture);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, FeatureInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.featureid = iprot.readString();
        struct.setFeatureidIsSet(true);
      }
      if (incoming.get(1)) {
        struct.feture = iprot.readBinary();
        struct.setFetureIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

