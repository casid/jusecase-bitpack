package org.jusecase.bitpack;

import java.util.Collection;

public interface BitWriter {

    void flush();

    BitProtocol getProtocol();

    void writeBoolean(boolean value);

    void writeByte(byte value);

    void writeBytesNonNull(byte[] values);

    void writeInt8(int value);

    void writeInt12(int value);

    void writeInt16(int value);

    void writeInt32(int value);

    void writeLong(long value);

    void writeStringNullable(String value);

    void writeStringNonNull(String value);

    default void writeObjectNullable(Object object) {
        if (object == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeObjectNonNull(object);
        }
    }

    @SuppressWarnings("unchecked")
    default void writeObjectNonNull(Object object) {
        BitSerializer serializer = getProtocol().getSerializer(object);
        serializer.serialize(this, object);
    }

    default void writeObjectsWithSameType(Collection<?> objects) {
        if (objects == null) {
            writeInt32(-1);
        } else {
            writeInt32(objects.size());
            for (Object object : objects) {
                writeObjectNullable(object);
            }
        }
    }

    default void writeObjectsWithDifferentTypes(Collection<?> objects) {
        writeObjectsWithDifferentTypes(objects, getProtocol().getBitTypes());
    }

    default void writeObjectsWithDifferentTypes(Collection<?> objects, BitTypes types) {
        if (objects == null) {
            writeInt32(-1);
        } else {
            writeInt32(objects.size());
            for (Object object : objects) {
                if (object == null) {
                    writeInt8(-1);
                } else {
                    writeInt8(types.getTypeForInstance(object));
                    writeObjectNonNull(object);
                }
            }
        }
    }
}
