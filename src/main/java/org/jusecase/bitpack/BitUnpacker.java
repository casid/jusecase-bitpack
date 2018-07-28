package org.jusecase.bitpack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface BitUnpacker {

    BitProtocol getProtocol();

    boolean unpackBoolean();

    byte unpackByte();

    byte[] unpackBytesNonNull();

    int unpackInt8();

    int unpackInt12();

    int unpackInt16();

    int unpackInt32();

    long unpackLong();

    String unpackStringNullable();

    String unpackStringNonNull();

    default <T> T unpackObjectNullable(Class<T> objectClass) {
        boolean isPresent = unpackBoolean();
        if (isPresent) {
            return unpackObjectNonNull(objectClass);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    default <T> T unpackObjectNonNull(Class<T> objectClass) {
        BitSerializer<?> serializer = getProtocol().getSerializer(objectClass);
        if (serializer == null) {
            throw new NullPointerException("No serializer found for class " + objectClass);
        }
        return (T) serializer.deserialize(this);
    }

    default <T> List<T> unpackObjectsWithSameTypeAsList(Class<T> sameType) {
        int size = unpackInt32();
        if (size < 0) {
            return null;
        }

        if (size == 0) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(size);
        unpackObjectsWithSameType(sameType, result, size);
        return result;
    }

    default <T> void unpackObjectsWithSameType(Class<T> sameType, Collection<T> result, int size) {
        for (int i = 0; i < size; ++i) {
            result.add(unpackObjectNullable(sameType));
        }
    }

    default <T> List<T> unpackObjectsWithDifferentTypesAsList() {
        int size = unpackInt32();
        if (size < 0) {
            return null;
        }

        if (size == 0) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(size);
        unpackObjectsWithDifferentTypes(result, size);
        return result;
    }

    @SuppressWarnings("unchecked")
    default <T> void unpackObjectsWithDifferentTypes(Collection<T> result, int size) {
        for (int i = 0; i < size; ++i) {
            int type = unpackInt8();
            if (type == -1) {
                result.add(null);
            } else {
                Class<?> subClass = getProtocol().getBitTypes().getClassForType(type);
                result.add((T) unpackObjectNonNull(subClass));
            }
        }
    }
}
