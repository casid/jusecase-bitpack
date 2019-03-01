package org.jusecase.bitpack;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("unused") // public api
public interface BitWriter {

    void flush();

    BitProtocol getProtocol();

    void writeBoolean(boolean value);

    void writeByte(byte value);

    void writeBytesNonNull(int lengthBits, byte[] values);

    void writeUnsignedInt(int bits, int value);

    void writeUnsignedInt2(int value);

    void writeUnsignedInt3(int value);

    void writeUnsignedInt4(int value);

    void writeUnsignedInt5(int value);

    void writeUnsignedInt6(int value);

    void writeUnsignedInt7(int value);

    void writeUnsignedInt8(int value);

    void writeUnsignedInt9(int value);

    void writeUnsignedInt10(int value);

    void writeUnsignedInt11(int value);

    void writeUnsignedInt12(int value);

    void writeUnsignedInt13(int value);

    void writeUnsignedInt14(int value);

    void writeUnsignedInt15(int value);

    void writeUnsignedInt16(int value);

    void writeInt8(int value);

    void writeInt12(int value);

    void writeInt16(int value);

    void writeInt32(int value);

    void writeLong(long value);

    void writeStringNullable(int lengthBits, String value);

    void writeStringNonNull(int lengthBits, String value);

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

    default void writeObjectsWithSameType(int lengthBits, Collection<?> objects) {
        if (objects == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeUnsignedInt(lengthBits, objects.size());
            for (Object object : objects) {
                writeObjectNullable(object);
            }
        }
    }

    default void writeObjectsWithSameType(int lengthBits, Object[] objects) {
        if (objects == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeUnsignedInt(lengthBits, objects.length);
            for (Object object : objects) {
                writeObjectNullable(object);
            }
        }
    }

    default void writeObjectsWithDifferentTypes(int lengthBits, Collection<?> objects) {
        writeObjectsWithDifferentTypes(lengthBits, objects, getProtocol().getBitTypes());
    }

    default void writeObjectsWithDifferentTypes(int lengthBits, Collection<?> objects, BitTypes types) {
        if (objects == null) {
            writeBoolean(false);
        } else {
            int objectBits = types.getRequiredBits();

            writeBoolean(true);
            writeUnsignedInt(lengthBits, objects.size());
            for (Object object : objects) {
                if (object == null) {
                    writeUnsignedInt(objectBits, 0);
                } else {
                    writeUnsignedInt(objectBits, types.getTypeForInstance(object));
                    writeObjectNonNull(object);
                }
            }
        }
    }

    default void writeUuidNonNull(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    default void writeInetAddress(InetAddress address) {
        if (address == null) {
            writeUnsignedInt8(0);
        } else {
            byte[] bytes = address.getAddress();
            writeUnsignedInt8(bytes.length);
            for (byte b : bytes) {
                writeByte(b);
            }
        }
    }

    default void writeInetSocketAddress(InetSocketAddress address) {
        if (address == null) {
            writeUnsignedInt8(0);
        } else {
            writeInetAddress(address.getAddress());
            writeUnsignedInt16(address.getPort());
        }
    }
}
