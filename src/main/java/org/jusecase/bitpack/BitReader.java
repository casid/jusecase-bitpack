package org.jusecase.bitpack;

import java.util.*;

public interface BitReader {

    BitProtocol getProtocol();

    boolean readBoolean();

    byte readByte();

    byte[] readBytesNonNull();

    int readInt8();

    int readInt12();

    int readInt16();

    int readInt32();

    long readLong();

    String readStringNullable();

    String readStringNonNull();

    default <T> T readObjectNullable(Class<T> objectClass) {
        boolean isPresent = readBoolean();
        if (isPresent) {
            return readObjectNonNull(objectClass);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    default <T> T readObjectNonNull(Class<T> objectClass) {
        BitSerializer<T> serializer = (BitSerializer<T>)getProtocol().getSerializer(objectClass);
        if (serializer == null) {
            throw new NullPointerException("No serializer found for class " + objectClass);
        }

        T object = serializer.createObject();
        serializer.deserialize(this, object);
        return object;
    }

    default <T> List<T> readObjectsWithSameTypeAsList(Class<T> sameType) {
        int size = readInt32();
        if (size < 0) {
            return null;
        }

        if (size == 0) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(size);
        readObjectsWithSameType(sameType, result, size);
        return result;
    }

    default <T> void readObjectsWithSameType(Class<T> sameType, Collection<T> result, int size) {
        for (int i = 0; i < size; ++i) {
            result.add(readObjectNullable(sameType));
        }
    }

    default <T> List<T> readObjectsWithDifferentTypesAsList() {
        int size = readInt32();
        if (size < 0) {
            return null;
        }

        if (size == 0) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(size);
        readObjectsWithDifferentTypes(result, size);
        return result;
    }

    @SuppressWarnings("unchecked")
    default <T> void readObjectsWithDifferentTypes(Collection<T> result, int size) {
        for (int i = 0; i < size; ++i) {
            int type = readInt8();
            if (type == -1) {
                result.add(null);
            } else {
                Class<?> subClass = getProtocol().getBitTypes().getClassForType(type);
                result.add((T) readObjectNonNull(subClass));
            }
        }
    }

    default UUID readUuidNonNull() {
        return new UUID(readLong(), readLong());
    }
}
