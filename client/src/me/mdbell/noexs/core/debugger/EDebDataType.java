package me.mdbell.noexs.core.debugger;

import java.util.function.BiConsumer;
import java.util.function.Function;

import me.mdbell.noexs.core.IConnection;

public enum EDebDataType {
    BYTE(byte.class, conn -> (byte)conn.readByte(), (conn, x) -> conn.writeByte((byte) x)),
    SHORT(short.class, conn -> (short)conn.readShort(), (conn, x) -> conn.writeShort((short) x)),
    INT(int.class, conn -> (int)conn.readInt(), (conn, x) -> conn.writeInt((int) x)),
    LONG(long.class, conn -> (long)conn.readLong(), (conn, x) -> conn.writeLong((long) x));

    private Class<?> cls;

    private Function<IConnection, Object> readValueMethod;
    private BiConsumer<IConnection, Object> writeValueMethod;

    private EDebDataType(Class<?> cls, Function<IConnection, Object> readValueMethod,
            BiConsumer<IConnection, Object> writeValueMethod) {
        this.cls = cls;
        this.readValueMethod = readValueMethod;
        this.writeValueMethod = writeValueMethod;
    }

    public Function<IConnection, Object> getReadValueMethod() {
        return readValueMethod;
    }

    public BiConsumer<IConnection, Object> getWriteValueMethod() {
        return writeValueMethod;
    }

    public static EDebDataType getDatatype(Class<?> cls) {
        EDebDataType res = null;
        for (EDebDataType datatype : EDebDataType.values()) {
            if (datatype.cls.equals(cls)) {
                res = datatype;
            }
        }
        return res;
    }
}
