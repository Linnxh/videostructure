/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.sensing.core.thrift.cap.bean;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "all"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-04-28")
public class _capRECT implements org.apache.thrift.TBase<_capRECT, _capRECT._Fields>, java.io.Serializable, Cloneable, Comparable<_capRECT> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("_capRECT");

  private static final org.apache.thrift.protocol.TField LEFT_FIELD_DESC = new org.apache.thrift.protocol.TField("left", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField TOP_FIELD_DESC = new org.apache.thrift.protocol.TField("top", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField RIGHT_FIELD_DESC = new org.apache.thrift.protocol.TField("right", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField BOTTOM_FIELD_DESC = new org.apache.thrift.protocol.TField("bottom", org.apache.thrift.protocol.TType.I32, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new _capRECTStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new _capRECTTupleSchemeFactory();

  public int left; // required
  public int top; // required
  public int right; // required
  public int bottom; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    LEFT((short)1, "left"),
    TOP((short)2, "top"),
    RIGHT((short)3, "right"),
    BOTTOM((short)4, "bottom");

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
        case 1: // LEFT
          return LEFT;
        case 2: // TOP
          return TOP;
        case 3: // RIGHT
          return RIGHT;
        case 4: // BOTTOM
          return BOTTOM;
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
  private static final int __LEFT_ISSET_ID = 0;
  private static final int __TOP_ISSET_ID = 1;
  private static final int __RIGHT_ISSET_ID = 2;
  private static final int __BOTTOM_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.LEFT, new org.apache.thrift.meta_data.FieldMetaData("left", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TOP, new org.apache.thrift.meta_data.FieldMetaData("top", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.RIGHT, new org.apache.thrift.meta_data.FieldMetaData("right", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.BOTTOM, new org.apache.thrift.meta_data.FieldMetaData("bottom", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(_capRECT.class, metaDataMap);
  }

  public _capRECT() {
  }

  public _capRECT(
    int left,
    int top,
    int right,
    int bottom)
  {
    this();
    this.left = left;
    setLeftIsSet(true);
    this.top = top;
    setTopIsSet(true);
    this.right = right;
    setRightIsSet(true);
    this.bottom = bottom;
    setBottomIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public _capRECT(_capRECT other) {
    __isset_bitfield = other.__isset_bitfield;
    this.left = other.left;
    this.top = other.top;
    this.right = other.right;
    this.bottom = other.bottom;
  }

  public _capRECT deepCopy() {
    return new _capRECT(this);
  }

  @Override
  public void clear() {
    setLeftIsSet(false);
    this.left = 0;
    setTopIsSet(false);
    this.top = 0;
    setRightIsSet(false);
    this.right = 0;
    setBottomIsSet(false);
    this.bottom = 0;
  }

  public int getLeft() {
    return this.left;
  }

  public _capRECT setLeft(int left) {
    this.left = left;
    setLeftIsSet(true);
    return this;
  }

  public void unsetLeft() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __LEFT_ISSET_ID);
  }

  /** Returns true if field left is set (has been assigned a value) and false otherwise */
  public boolean isSetLeft() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __LEFT_ISSET_ID);
  }

  public void setLeftIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __LEFT_ISSET_ID, value);
  }

  public int getTop() {
    return this.top;
  }

  public _capRECT setTop(int top) {
    this.top = top;
    setTopIsSet(true);
    return this;
  }

  public void unsetTop() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TOP_ISSET_ID);
  }

  /** Returns true if field top is set (has been assigned a value) and false otherwise */
  public boolean isSetTop() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TOP_ISSET_ID);
  }

  public void setTopIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TOP_ISSET_ID, value);
  }

  public int getRight() {
    return this.right;
  }

  public _capRECT setRight(int right) {
    this.right = right;
    setRightIsSet(true);
    return this;
  }

  public void unsetRight() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __RIGHT_ISSET_ID);
  }

  /** Returns true if field right is set (has been assigned a value) and false otherwise */
  public boolean isSetRight() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __RIGHT_ISSET_ID);
  }

  public void setRightIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __RIGHT_ISSET_ID, value);
  }

  public int getBottom() {
    return this.bottom;
  }

  public _capRECT setBottom(int bottom) {
    this.bottom = bottom;
    setBottomIsSet(true);
    return this;
  }

  public void unsetBottom() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __BOTTOM_ISSET_ID);
  }

  /** Returns true if field bottom is set (has been assigned a value) and false otherwise */
  public boolean isSetBottom() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __BOTTOM_ISSET_ID);
  }

  public void setBottomIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __BOTTOM_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case LEFT:
      if (value == null) {
        unsetLeft();
      } else {
        setLeft((java.lang.Integer)value);
      }
      break;

    case TOP:
      if (value == null) {
        unsetTop();
      } else {
        setTop((java.lang.Integer)value);
      }
      break;

    case RIGHT:
      if (value == null) {
        unsetRight();
      } else {
        setRight((java.lang.Integer)value);
      }
      break;

    case BOTTOM:
      if (value == null) {
        unsetBottom();
      } else {
        setBottom((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case LEFT:
      return getLeft();

    case TOP:
      return getTop();

    case RIGHT:
      return getRight();

    case BOTTOM:
      return getBottom();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case LEFT:
      return isSetLeft();
    case TOP:
      return isSetTop();
    case RIGHT:
      return isSetRight();
    case BOTTOM:
      return isSetBottom();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof _capRECT)
      return this.equals((_capRECT)that);
    return false;
  }

  public boolean equals(_capRECT that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_left = true;
    boolean that_present_left = true;
    if (this_present_left || that_present_left) {
      if (!(this_present_left && that_present_left))
        return false;
      if (this.left != that.left)
        return false;
    }

    boolean this_present_top = true;
    boolean that_present_top = true;
    if (this_present_top || that_present_top) {
      if (!(this_present_top && that_present_top))
        return false;
      if (this.top != that.top)
        return false;
    }

    boolean this_present_right = true;
    boolean that_present_right = true;
    if (this_present_right || that_present_right) {
      if (!(this_present_right && that_present_right))
        return false;
      if (this.right != that.right)
        return false;
    }

    boolean this_present_bottom = true;
    boolean that_present_bottom = true;
    if (this_present_bottom || that_present_bottom) {
      if (!(this_present_bottom && that_present_bottom))
        return false;
      if (this.bottom != that.bottom)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + left;

    hashCode = hashCode * 8191 + top;

    hashCode = hashCode * 8191 + right;

    hashCode = hashCode * 8191 + bottom;

    return hashCode;
  }

  @Override
  public int compareTo(_capRECT other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetLeft()).compareTo(other.isSetLeft());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLeft()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.left, other.left);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTop()).compareTo(other.isSetTop());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTop()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.top, other.top);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetRight()).compareTo(other.isSetRight());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRight()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.right, other.right);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetBottom()).compareTo(other.isSetBottom());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBottom()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.bottom, other.bottom);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("_capRECT(");
    boolean first = true;

    sb.append("left:");
    sb.append(this.left);
    first = false;
    if (!first) sb.append(", ");
    sb.append("top:");
    sb.append(this.top);
    first = false;
    if (!first) sb.append(", ");
    sb.append("right:");
    sb.append(this.right);
    first = false;
    if (!first) sb.append(", ");
    sb.append("bottom:");
    sb.append(this.bottom);
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

  private static class _capRECTStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public _capRECTStandardScheme getScheme() {
      return new _capRECTStandardScheme();
    }
  }

  private static class _capRECTStandardScheme extends org.apache.thrift.scheme.StandardScheme<_capRECT> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, _capRECT struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // LEFT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.left = iprot.readI32();
              struct.setLeftIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TOP
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.top = iprot.readI32();
              struct.setTopIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // RIGHT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.right = iprot.readI32();
              struct.setRightIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // BOTTOM
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.bottom = iprot.readI32();
              struct.setBottomIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, _capRECT struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(LEFT_FIELD_DESC);
      oprot.writeI32(struct.left);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(TOP_FIELD_DESC);
      oprot.writeI32(struct.top);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(RIGHT_FIELD_DESC);
      oprot.writeI32(struct.right);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(BOTTOM_FIELD_DESC);
      oprot.writeI32(struct.bottom);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class _capRECTTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public _capRECTTupleScheme getScheme() {
      return new _capRECTTupleScheme();
    }
  }

  private static class _capRECTTupleScheme extends org.apache.thrift.scheme.TupleScheme<_capRECT> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, _capRECT struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetLeft()) {
        optionals.set(0);
      }
      if (struct.isSetTop()) {
        optionals.set(1);
      }
      if (struct.isSetRight()) {
        optionals.set(2);
      }
      if (struct.isSetBottom()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetLeft()) {
        oprot.writeI32(struct.left);
      }
      if (struct.isSetTop()) {
        oprot.writeI32(struct.top);
      }
      if (struct.isSetRight()) {
        oprot.writeI32(struct.right);
      }
      if (struct.isSetBottom()) {
        oprot.writeI32(struct.bottom);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, _capRECT struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.left = iprot.readI32();
        struct.setLeftIsSet(true);
      }
      if (incoming.get(1)) {
        struct.top = iprot.readI32();
        struct.setTopIsSet(true);
      }
      if (incoming.get(2)) {
        struct.right = iprot.readI32();
        struct.setRightIsSet(true);
      }
      if (incoming.get(3)) {
        struct.bottom = iprot.readI32();
        struct.setBottomIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

