package org.jusecase.bitpack;

import java.util.Collection;

public interface BitPacker {

    void flush();

    BitProtocol getProtocol();

    void packBoolean(boolean value);

    void packByte(byte value);

    void packBytesNonNull(byte[] values);

    void packInt8(int value);

    void packInt12(int value);

    void packInt16(int value);

    void packInt32(int value);

    void packLong(long value);

    void packStringNullable(String value);

    void packStringNonNull(String value);

    default void packObjectNullable(Object object) {
        if (object == null) {
            packBoolean(false);
        } else {
            packBoolean(true);
            packObjectNonNull(object);
        }
    }

    @SuppressWarnings("unchecked")
    default void packObjectNonNull(Object object) {
        BitSerializer serializer = getProtocol().getSerializer(object);
        serializer.serialize(this, object);
    }

    default void packObjectsWithSameType(Collection<?> objects) {
        if (objects == null) {
            packInt32(-1);
        } else {
            packInt32(objects.size());
            for (Object object : objects) {
                packObjectNullable(object);
            }
        }
    }

    default void packObjectsWithDifferentTypes(Collection<?> objects) {
        packObjectsWithDifferentTypes(objects, getProtocol().getBitTypes());
    }

    default void packObjectsWithDifferentTypes(Collection<?> objects, BitTypes types) {
        if (objects == null) {
            packInt32(-1);
        } else {
            packInt32(objects.size());
            for (Object object : objects) {
                if (object == null) {
                    packInt8(-1);
                } else {
                    packInt8(types.getTypeForInstance(object));
                    packObjectNonNull(object);
                }
            }
        }
    }
}
