package org.hbase.async;

import java.util.ArrayList;

import com.google.protobuf.ByteString;

import org.hbase.async.generated.ClientPB;
import org.jboss.netty.buffer.ChannelBuffer;
import org.hbase.async.generated.ClientPB.MutateRequest;
import org.hbase.async.generated.ClientPB.MutationProto;
import org.hbase.async.generated.HBasePB;

public final class AppendRequest extends BatchableRpc
  implements HBaseRpc.HasTable, HBaseRpc.HasKey, HBaseRpc.HasFamily,
             HBaseRpc.HasQualifiers, HBaseRpc.HasValues, HBaseRpc.IsEdit,
             /* legacy: */ HBaseRpc.HasQualifier, HBaseRpc.HasValue {

  private static final byte[] APPEND = new byte[] { 'a', 'p', 'p', 'e', 'n', 'd' };
  private static final byte[] RETURN_RESULTS = new byte[] {'_', 'r', 'r', '_'};
  
  /** Code type used for serialized `Append' objects.  */
  static final byte CODE = 78;

  /**
   * Invariants:
   *   - qualifiers.length == values.length
   *   - qualifiers.length > 0
   */
  private final byte[][] qualifiers;
  private final byte[][] values;
  
  /** Whether or not to return the result of the append request */
  private boolean return_result = false;

  public AppendRequest(final byte[] table,
                    final byte[] key,
                    final byte[] family,
                    final byte[] qualifier,
                    final byte[] value) {
    this(table, key, family, qualifier, value, KeyValue.TIMESTAMP_NOW, 
        RowLock.NO_LOCK);
  }

  public AppendRequest(final byte[] table,
                    final byte[] key,
                    final byte[] family,
                    final byte[][] qualifiers,
                    final byte[][] values) {
    this(table, key, family, qualifiers, values, KeyValue.TIMESTAMP_NOW, 
        RowLock.NO_LOCK, false);
  }

  public AppendRequest(final byte[] table,
                    final byte[] key,
                    final byte[] family,
                    final byte[] qualifier,
                    final byte[] value,
                    final long timestamp) {
    this(table, key, family, qualifier, value, timestamp, RowLock.NO_LOCK);
  }

  public AppendRequest(final byte[] table,
                    final byte[] key,
                    final byte[] family,
                    final byte[][] qualifiers,
                    final byte[][] values,
                    final long timestamp) {
    this(table, key, family, qualifiers, values, timestamp, RowLock.NO_LOCK, false);
  }

  public AppendRequest(final byte[] table,
                    final byte[] key,
                    final byte[] family,
                    final byte[] qualifier,
                    final byte[] value,
                    final RowLock lock) {
    this(table, key, family, qualifier, value, KeyValue.TIMESTAMP_NOW, lock.id());
  }

  public AppendRequest(final byte[] table,
                    final byte[] key,
                    final byte[] family,
                    final byte[] qualifier,
                    final byte[] value,
                    final long timestamp,
                    final RowLock lock) {
    this(table, key, family, qualifier, value, timestamp, lock.id());
  }

  public AppendRequest(final byte[] table,
                    final byte[] key,
                    final byte[] family,
                    final byte[][] qualifiers,
                    final byte[][] values,
                    final long timestamp,
                    final RowLock lock) {
    this(table, key, family, qualifiers, values, timestamp, lock.id(), false);
  }

  public AppendRequest(final String table,
                    final String key,
                    final String family,
                    final String qualifier,
                    final String value) {
    this(table.getBytes(), key.getBytes(), family.getBytes(),
         qualifier.getBytes(), value.getBytes(),
         KeyValue.TIMESTAMP_NOW, RowLock.NO_LOCK);
  }

  public AppendRequest(final String table,
                    final String key,
                    final String family,
                    final String qualifier,
                    final String value,
                    final RowLock lock) {
    this(table.getBytes(), key.getBytes(), family.getBytes(),
         qualifier.getBytes(), value.getBytes(),
         KeyValue.TIMESTAMP_NOW, lock.id());
  }

  public AppendRequest(final byte[] table,
                    final KeyValue kv) {
    this(table, kv, RowLock.NO_LOCK);
  }

  public AppendRequest(final byte[] table,
                    final KeyValue kv,
                    final RowLock lock) {
    this(table, kv, lock.id());
  }

  /** Private constructor.  */
  private AppendRequest(final byte[] table,
                     final KeyValue kv,
                     final long lockid) {
    super(table, kv.key(), kv.family(), kv.timestamp(), lockid);
    this.qualifiers = new byte[][] { kv.qualifier() };
    this.values = new byte[][] { kv.value() };
  }

  /** Private constructor.  */
  private AppendRequest(final byte[] table,
                     final byte[] key,
                     final byte[] family,
                     final byte[] qualifier,
                     final byte[] value,
                     final long timestamp,
                     final long lockid) {
    this(table, key, family, new byte[][] { qualifier }, new byte[][] { value },
         timestamp, lockid, false);
  }

  /** Private constructor.  */
  private AppendRequest(final byte[] table,
                     final byte[] key,
                     final byte[] family,
                     final byte[][] qualifiers,
                     final byte[][] values,
                     final long timestamp,
                     final long lockid,
                     final boolean return_result) {
    super(table, key, family, timestamp, lockid);
    KeyValue.checkFamily(family);
    
    if (qualifiers.length != values.length) {
      throw new IllegalArgumentException("Have " + qualifiers.length
        + " qualifiers and " + values.length + " values.  Should be equal.");
    } else if (qualifiers.length == 0) {
      throw new IllegalArgumentException("Need at least one qualifier/value.");
    }
    for (int i = 0; i < qualifiers.length; i++) {
      KeyValue.checkQualifier(qualifiers[i]);
      KeyValue.checkValue(values[i]);
    }
    this.qualifiers = qualifiers;
    this.values = values;
  }

  @Override
  byte[] method(final byte server_version) {
    if (server_version >= RegionClient.SERVER_VERSION_095_OR_ABOVE) {
      return MUTATE;
    }
    return APPEND;
  }

  @Override
  public byte[] table() {
    return table;
  }

  @Override
  public byte[] key() {
    return key;
  }

  /**
   * Returns the first qualifier of the set of edits in this RPC.
   * {@inheritDoc}
   */
  @Override
  public byte[] qualifier() {
    return qualifiers[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[][] qualifiers() {
    return qualifiers;
  }

  /**
   * Returns the first value of the set of edits in this RPC.
   * {@inheritDoc}
   */
  @Override
  public byte[] value() {
    return values[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[][] values() {
    return values;
  }
  
  public String toString() {
    return super.toStringWithQualifiers("AppendRequest",
                                       family, qualifiers, values,
                                       ", timestamp=" + timestamp
                                       + ", lockid=" + lockid
                                       + ", durable=" + durable
                                       + ", return_result=" + return_result
                                       + ", bufferable=" + super.bufferable);
  }

  /** @param return_result Whether or not to fetch the results of the append
   * from HBase. */
  public void returnResult(boolean return_result) {
    this.return_result = return_result;
  }
  
  /** @return Whether or not to fetch the results of the append from HBase. */
  public boolean returnResult() {
    return return_result;
  }
  
  // ---------------------- //
  // Package private stuff. //
  // ---------------------- //

  @Override
  byte version(final byte server_version) {
    // Versions are:
    //   1: Before 0.92.0, if we're serializing a `multiPut' RPC.
    //   2: HBASE-3921 in 0.92.0 added "attributes" at the end.
    if (server_version >= RegionClient.SERVER_VERSION_092_OR_ABOVE) {
      return 2;
    } else {
      return 1;
    }
  }

  @Override
  byte code() {
    return CODE;
  }

  @Override
  int numKeyValues() {
    return qualifiers.length;
  }

  @Override
  int payloadSize() {
    int size = 0;
    for (int i = 0; i < qualifiers.length; i++) {
      size += KeyValue.predictSerializedSize(key, family, qualifiers[i], values[i]);
    }
    return size;
  }

  @Override
  void serializePayload(final ChannelBuffer buf) {
    for (int i = 0; i < qualifiers.length; i++) {
      //HBASE KeyValue (org.apache.hadoop.hbase.KeyValue) doesn't have an Append Type
      KeyValue.serialize(buf, KeyValue.PUT, timestamp, key, family,
                         qualifiers[i], values[i]);
    }
  }

  private int predictSerializedSize() {
    int size = 0;
    size += 4;  // int:  Number of parameters.
    size += 1;  // byte: Type of the 1st parameter.
    size += 3;  // vint: region name length (3 bytes => max length = 32768).
    size += region.name().length;  // The region name.

    size += predictAppendSize();
    size += 4;  // int: Number of attributes
    size += 4;  // int: length of the attribute name
    size += 4;  // char[]: attribute name
    size += 1;  // vint: attribute length
    size += 1;  // vint: attribute value
    return size;
  }

  /** The raw size of the underlying `Append'.  */
  int predictAppendSize() {
    int size = 0;
    size += 1;  // byte: Type of the 2nd parameter.
    size += 1;  // byte: Type again (see HBASE-2877).

    size += 1;  // byte: Version of Append.
    size += 3;  // vint: row key length (3 bytes => max length = 32768).
    size += key.length;  // The row key.
    size += 8;  // long: Timestamp.
    size += 8;  // long: Lock ID.
    size += 1;  // bool: Whether or not to write to the WAL.
    size += 4;  // int:  Number of families for which we have edits.

    size += 1;  // vint: Family length (guaranteed on 1 byte).
    size += family.length;  // The family.
    size += 4;  // int:  Number of KeyValues that follow.
    size += 4;  // int:  Total number of bytes for all those KeyValues.

    size += payloadSize();

    return size;
  }

  @Override
  MutationProto toMutationProto() {
    final MutationProto.ColumnValue.Builder columns =  // All columns ...
      MutationProto.ColumnValue.newBuilder()
      .setFamily(Bytes.wrap(family));                  // ... for this family.

    // Now add all the qualifier-value pairs.
    for (int i = 0; i < qualifiers.length; i++) {
      final MutationProto.ColumnValue.QualifierValue column =
        MutationProto.ColumnValue.QualifierValue.newBuilder()
        .setQualifier(Bytes.wrap(qualifiers[i]))
        .setValue(Bytes.wrap(values[i]))
        .setTimestamp(timestamp)
        .build();
      columns.addQualifierValue(column);
    }

    final MutationProto.Builder append = MutationProto.newBuilder()
      .setRow(Bytes.wrap(key))
      .setMutateType(MutationProto.MutationType.APPEND)
      .addColumnValue(columns);
    if (!durable) {
      append.setDurability(MutationProto.Durability.SKIP_WAL);
    }
    
    //Set return results flag
    HBasePB.NameBytesPair nameBytePair = HBasePB.NameBytesPair.getDefaultInstance();
    append.addAttribute(nameBytePair.toBuilder().
            setNameBytes(ByteString.copyFrom(RETURN_RESULTS)).
            setValue(ByteString.copyFrom(
                return_result ? new byte[] { -1 } : new byte[] { 0 })));
    
    return append.build();
  }

  /** Serializes this request.  */
  @Override
  ChannelBuffer serialize(final byte server_version) {
    if (server_version < RegionClient.SERVER_VERSION_095_OR_ABOVE) {
      return serializeOld(server_version);
    }

    final MutateRequest req = MutateRequest.newBuilder()
      .setRegion(region.toProtobuf())
      .setMutation(toMutationProto())
      .build();
    return toChannelBuffer(MUTATE, req);
  }

  /** Serializes this request for HBase 0.94 and before.  */
  private ChannelBuffer serializeOld(final byte server_version) {
    final ChannelBuffer buf = newBuffer(server_version,
                                        predictSerializedSize());
    buf.writeInt(2);  // Number of parameters.

    // 1st param: byte array containing region name
    writeHBaseByteArray(buf, region.name());

    // 2nd param: Append object
    serializeInto(buf);

    return buf;
  }

  /** Serialize the raw underlying `Append' into the given buffer.  */
  void serializeInto(final ChannelBuffer buf) {
    buf.writeByte(CODE); // Code for a `Append' parameter.
    buf.writeByte(CODE); // Code again (see HBASE-2877).
    buf.writeByte(1);    // Append#APPENDT_VERSION.  Stick to v1 here for now.
    writeByteArray(buf, key);  // The row key.

    buf.writeLong(timestamp);  // Timestamp.

    buf.writeLong(lockid);    // Lock ID.
    buf.writeByte(durable ? 0x01 : 0x00);  // Whether or not to use the WAL.

    buf.writeInt(1);  // Number of families that follow.
    writeByteArray(buf, family);  // The column family.

    buf.writeInt(qualifiers.length);  // Number of "KeyValues" that follow.
    buf.writeInt(payloadSize());  // Size of the KV that follows.
    serializePayload(buf);
    buf.writeInt(1);    // Set one attribute
    buf.writeInt(4);    // Set attribute name length
    buf.writeBytes(RETURN_RESULTS);
    buf.writeByte(1);
    buf.writeByte(this.return_result ? 1:0);
  }

  @Override
  Object deserialize(ChannelBuffer buf, int cell_size) {
    if (!this.return_result) {
      HBaseRpc.ensureNoCell(cell_size);
    }
    final ClientPB.MutateResponse resp = 
        readProtobuf(buf, ClientPB.MutateResponse.PARSER);
    final ClientPB.Result result = resp.getResult();
    if (this.return_result) {
      final ArrayList<KeyValue> kvs = GetRequest.convertResult(result,
          buf, cell_size);
      if (kvs.size() != 1) {
        throw new InvalidResponseException("Atomic increment returned "
          + kvs.size() + " KeyValue(s), but we expected exactly one. kvs="
          + kvs, resp);
      }
      return kvs.get(0);
    } else {
      return null;
    }
  }
}