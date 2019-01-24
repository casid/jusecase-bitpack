package org.jusecase.bitpack;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings("unused") // public api
public interface BitReader {

    BitProtocol getProtocol();

    boolean readBoolean();

    byte readByte();

    byte[] readBytesNonNull();

    int readUnsignedInt(int bits);

    int readUnsignedInt2();

    int readUnsignedInt3();

    int readUnsignedInt4();

    int readUnsignedInt5();

    int readUnsignedInt6();

    int readUnsignedInt7();

    int readUnsignedInt8();

    int readUnsignedInt9();

    int readUnsignedInt10();

    int readUnsignedInt11();

    int readUnsignedInt12();

    int readUnsignedInt13();

    int readUnsignedInt14();

    int readUnsignedInt15();

    int readUnsignedInt16();

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
        BitSerializer<T> serializer = (BitSerializer<T>) getProtocol().getSerializer(objectClass);
        if (serializer == null) {
            throw new NullPointerException("No serializer found for class " + objectClass);
        }

        return serializer.deserialize(this);
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

    @SuppressWarnings("unchecked")
    default <T> T[] readObjectsWithSameTypeAsArray(Class<T> sameType) {
        int size = readInt32();
        if (size < 0) {
            return null;
        }

        T[] result = (T[]) Array.newInstance(sameType, size);
        for (int i = 0; i < size; ++i) {
            result[i] = readObjectNullable(sameType);
        }
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

    default InetAddress readInetAddress() {
        int length = readUnsignedInt8();
        if (length == 0) {
            return null;
        }
        if (length != 4 && length != 16) {
            throw new IllegalArgumentException("Unknown ip address with " + length + " bytes");
        }
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytes[i] = readByte();
        }

        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Unknown ip address with " + length + " bytes");
        }
    }

    default InetSocketAddress readInetSocketAddress() {
        InetAddress inetAddress = readInetAddress();
        if (inetAddress == null) {
            return null;
        }

        return new InetSocketAddress(inetAddress, readUnsignedInt16());
    }
}
