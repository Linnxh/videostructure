/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.sensing.core.thrift.cmp.bean;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "all"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2018-07-03")
public class CmpRetInfo implements org.apache.thrift.TBase<CmpRetInfo, CmpRetInfo._Fields>, java.io.Serializable, Cloneable, Comparable<CmpRetInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CmpRetInfo");

  private static final org.apache.thrift.protocol.TField ERR_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("errCode", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField DBID_FIELD_DESC = new org.apache.thrift.protocol.TField("DBID", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField CPI_FIELD_DESC = new org.apache.thrift.protocol.TField("cpi", org.apache.thrift.protocol.TType.LIST, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CmpRetInfoStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CmpRetInfoTupleSchemeFactory();

  public int errCode; // required
  public int DBID; // required
  public java.util.List<CmpScoreInfo> cpi; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ERR_CODE((short)1, "errCode"),
    DBID((short)2, "DBID"),
    CPI((short)3, "cpi");

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
        case 1: // ERR_CODE
          return ERR_CODE;
        case 2: // DBID
          return DBID;
        case 3: // CPI
          return CPI;
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
  private static final int __ERRCODE_ISSET_ID = 0;
  private static final int __DBID_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ERR_CODE, new org.apache.thrift.meta_data.FieldMetaData("errCode", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.DBID, new org.apache.thrift.meta_data.FieldMetaData("DBID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.CPI, new org.apache.thrift.meta_data.FieldMetaData("cpi", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRUCT            , "CmpScoreInfo"))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CmpRetInfo.class, metaDataMap);
  }

  public CmpRetInfo() {
  }

  public CmpRetInfo(
    int errCode,
    int DBID,
    java.util.List<CmpScoreInfo> cpi)
  {
    this();
    this.errCode = errCode;
    setErrCodeIsSet(true);
    this.DBID = DBID;
    setDBIDIsSet(true);
    this.cpi = cpi;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CmpRetInfo(CmpRetInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    this.errCode = other.errCode;
    this.DBID = other.DBID;
    if (other.isSetCpi()) {
      java.util.List<CmpScoreInfo> __this__cpi = new java.util.ArrayList<CmpScoreInfo>(other.cpi.size());
      for (CmpScoreInfo other_element : other.cpi) {
        __this__cpi.add(other_element);
      }
      this.cpi = __this__cpi;
    }
  }

  public CmpRetInfo deepCopy() {
    return new CmpRetInfo(this);
  }

  @Override
  public void clear() {
    setErrCodeIsSet(false);
    this.errCode = 0;
    setDBIDIsSet(false);
    this.DBID = 0;
    this.cpi = null;
  }

  public int getErrCode() {
    return this.errCode;
  }

  public CmpRetInfo setErrCode(int errCode) {
    this.errCode = errCode;
    setErrCodeIsSet(true);
    return this;
  }

  public void unsetErrCode() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ERRCODE_ISSET_ID);
  }

  /** Returns true if field errCode is set (has been assigned a value) and false otherwise */
  public boolean isSetErrCode() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ERRCODE_ISSET_ID);
  }

  public void setErrCodeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ERRCODE_ISSET_ID, value);
  }

  public int getDBID() {
    return this.DBID;
  }

  public CmpRetInfo setDBID(int DBID) {
    this.DBID = DBID;
    setDBIDIsSet(true);
    return this;
  }

  public void unsetDBID() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __DBID_ISSET_ID);
  }

  /** Returns true if field DBID is set (has been assigned a value) and false otherwise */
  public boolean isSetDBID() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __DBID_ISSET_ID);
  }

  public void setDBIDIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __DBID_ISSET_ID, value);
  }

  public int getCpiSize() {
    return (this.cpi == null) ? 0 : this.cpi.size();
  }

  public java.util.Iterator<CmpScoreInfo> getCpiIterator() {
    return (this.cpi == null) ? null : this.cpi.iterator();
  }

  public void addToCpi(CmpScoreInfo elem) {
    if (this.cpi == null) {
      this.cpi = new java.util.ArrayList<CmpScoreInfo>();
    }
    this.cpi.add(elem);
  }

  public java.util.List<CmpScoreInfo> getCpi() {
    return this.cpi;
  }

  public CmpRetInfo setCpi(java.util.List<CmpScoreInfo> cpi) {
    this.cpi = cpi;
    return this;
  }

  public void unsetCpi() {
    this.cpi = null;
  }

  /** Returns true if field cpi is set (has been assigned a value) and false otherwise */
  public boolean isSetCpi() {
    return this.cpi != null;
  }

  public void setCpiIsSet(boolean value) {
    if (!value) {
      this.cpi = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case ERR_CODE:
      if (value == null) {
        unsetErrCode();
      } else {
        setErrCode((java.lang.Integer)value);
      }
      break;

    case DBID:
      if (value == null) {
        unsetDBID();
      } else {
        setDBID((java.lang.Integer)value);
      }
      break;

    case CPI:
      if (value == null) {
        unsetCpi();
      } else {
        setCpi((java.util.List<CmpScoreInfo>)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case ERR_CODE:
      return getErrCode();

    case DBID:
      return getDBID();

    case CPI:
      return getCpi();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case ERR_CODE:
      return isSetErrCode();
    case DBID:
      return isSetDBID();
    case CPI:
      return isSetCpi();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof CmpRetInfo)
      return this.equals((CmpRetInfo)that);
    return false;
  }

  public boolean equals(CmpRetInfo that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_errCode = true;
    boolean that_present_errCode = true;
    if (this_present_errCode || that_present_errCode) {
      if (!(this_present_errCode && that_present_errCode))
        return false;
      if (this.errCode != that.errCode)
        return false;
    }

    boolean this_present_DBID = true;
    boolean that_present_DBID = true;
    if (this_present_DBID || that_present_DBID) {
      if (!(this_present_DBID && that_present_DBID))
        return false;
      if (this.DBID != that.DBID)
        return false;
    }

    boolean this_present_cpi = true && this.isSetCpi();
    boolean that_present_cpi = true && that.isSetCpi();
    if (this_present_cpi || that_present_cpi) {
      if (!(this_present_cpi && that_present_cpi))
        return false;
      if (!this.cpi.equals(that.cpi))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + errCode;

    hashCode = hashCode * 8191 + DBID;

    hashCode = hashCode * 8191 + ((isSetCpi()) ? 131071 : 524287);
    if (isSetCpi())
      hashCode = hashCode * 8191 + cpi.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(CmpRetInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetErrCode()).compareTo(other.isSetErrCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetErrCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.errCode, other.errCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetDBID()).compareTo(other.isSetDBID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDBID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.DBID, other.DBID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetCpi()).compareTo(other.isSetCpi());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCpi()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.cpi, other.cpi);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CmpRetInfo(");
    boolean first = true;

    sb.append("errCode:");
    sb.append(this.errCode);
    first = false;
    if (!first) sb.append(", ");
    sb.append("DBID:");
    sb.append(this.DBID);
    first = false;
    if (!first) sb.append(", ");
    sb.append("cpi:");
    if (this.cpi == null) {
      sb.append("null");
    } else {
      sb.append(this.cpi);
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CmpRetInfoStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CmpRetInfoStandardScheme getScheme() {
      return new CmpRetInfoStandardScheme();
    }
  }

  private static class CmpRetInfoStandardScheme extends org.apache.thrift.scheme.StandardScheme<CmpRetInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CmpRetInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ERR_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.errCode = iprot.readI32();
              struct.setErrCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DBID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.DBID = iprot.readI32();
              struct.setDBIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // CPI
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.cpi = new java.util.ArrayList<CmpScoreInfo>(_list0.size);
                CmpScoreInfo _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = new CmpScoreInfo();
                  _elem1.read(iprot);
                  struct.cpi.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setCpiIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CmpRetInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ERR_CODE_FIELD_DESC);
      oprot.writeI32(struct.errCode);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(DBID_FIELD_DESC);
      oprot.writeI32(struct.DBID);
      oprot.writeFieldEnd();
      if (struct.cpi != null) {
        oprot.writeFieldBegin(CPI_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.cpi.size()));
          for (CmpScoreInfo _iter3 : struct.cpi)
          {
            _iter3.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CmpRetInfoTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CmpRetInfoTupleScheme getScheme() {
      return new CmpRetInfoTupleScheme();
    }
  }

  private static class CmpRetInfoTupleScheme extends org.apache.thrift.scheme.TupleScheme<CmpRetInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CmpRetInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetErrCode()) {
        optionals.set(0);
      }
      if (struct.isSetDBID()) {
        optionals.set(1);
      }
      if (struct.isSetCpi()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetErrCode()) {
        oprot.writeI32(struct.errCode);
      }
      if (struct.isSetDBID()) {
        oprot.writeI32(struct.DBID);
      }
      if (struct.isSetCpi()) {
        {
          oprot.writeI32(struct.cpi.size());
          for (CmpScoreInfo _iter4 : struct.cpi)
          {
            _iter4.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CmpRetInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.errCode = iprot.readI32();
        struct.setErrCodeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.DBID = iprot.readI32();
        struct.setDBIDIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.cpi = new java.util.ArrayList<CmpScoreInfo>(_list5.size);
          CmpScoreInfo _elem6;
          for (int _i7 = 0; _i7 < _list5.size; ++_i7)
          {
            _elem6 = new CmpScoreInfo();
            _elem6.read(iprot);
            struct.cpi.add(_elem6);
          }
        }
        struct.setCpiIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

