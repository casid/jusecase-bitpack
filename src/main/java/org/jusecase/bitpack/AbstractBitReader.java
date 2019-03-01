package org.jusecase.bitpack;

import java.nio.charset.StandardCharsets;

public abstract class AbstractBitReader implements BitReader {
    private final BitProtocol protocol;

    private long scratch;
    private int scratchBits;

    protected AbstractBitReader(BitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public BitProtocol getProtocol() {
        return protocol;
    }

    @Override
    public boolean readBoolean() {
        grabBitsIfRequired(1);
        boolean result = (scratch & 1) > 0;
        dropBits(1);

        return result;
    }

    @Override
    public byte readByte() {
        grabBitsIfRequired(8);
        byte result = (byte)scratch;
        dropBits(8);

        return result;
    }

    @Override
    public byte[] readBytesNonNull(int lengthBits) {
        int length = readUnsignedInt(lengthBits);
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytes[i] = readByte();
        }
        return bytes;
    }

    @Override
    public int readUnsignedInt2() {
        return readUnsignedInt(2);
    }

    @Override
    public int readUnsignedInt3() {
        return readUnsignedInt(3);
    }

    @Override
    public int readUnsignedInt4() {
        return readUnsignedInt(4);
    }

    @Override
    public int readUnsignedInt5() {
        return readUnsignedInt(5);
    }

    @Override
    public int readUnsignedInt6() {
        return readUnsignedInt(6);
    }

    @Override
    public int readUnsignedInt7() {
        return readUnsignedInt(7);
    }

    @Override
    public int readUnsignedInt8() {
        return readUnsignedInt(8);
    }

    @Override
    public int readUnsignedInt9() {
        return readUnsignedInt(9);
    }

    @Override
    public int readUnsignedInt10() {
        return readUnsignedInt(10);
    }

    @Override
    public int readUnsignedInt11() {
        return readUnsignedInt(11);
    }

    @Override
    public int readUnsignedInt12() {
        return readUnsignedInt(12);
    }

    @Override
    public int readUnsignedInt13() {
        return readUnsignedInt(13);
    }

    @Override
    public int readUnsignedInt14() {
        return readUnsignedInt(14);
    }

    @Override
    public int readUnsignedInt15() {
        return readUnsignedInt(15);
    }

    @Override
    public int readUnsignedInt16() {
        return readUnsignedInt(16);
    }

    @Override
    public int readInt8() {
        grabBitsIfRequired(8);
        int result = (byte)scratch;
        dropBits(8);

        return result;
    }

    @Override
    public int readInt12() {
        grabBitsIfRequired(12);
        int result = (int)scratch & 0x00000fff;
        if ((result & 0x00000800) == 0x00000800) {
            result |= 0xfffff000;
        }
        dropBits(12);

        return result;
    }

    @Override
    public int readInt16() {
        grabBitsIfRequired(16);
        int result = (int)scratch & 0x0000ffff;
        if ((result & 0x00008000) == 0x00008000) {
            result |= 0xffff0000;
        }
        dropBits(16);

        return result;
    }

    @Override
    public int readInt32() {
        grabBitsIfRequired(32);
        int result = (int)scratch;
        dropBits(32);

        return result;
    }

    @Override
    public long readLong() {
        long a = readInt32();
        long b = readInt32();
        return (a & 0x00000000FFFFFFFFL) | (b << 32);
    }

    @Override
    public String readStringNullable(int lengthBits) {
        boolean isNonNull = readBoolean();
        if (isNonNull) {
            return readStringNonNull(lengthBits);
        } else {
            return null;
        }
    }

    @Override
    public String readStringNonNull(int lengthBits) {
        byte[] bytes = readBytesNonNull(lengthBits);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected abstract byte get();

    @Override
    public int readUnsignedInt(int bits) {
        grabBitsIfRequired(bits);
        int mask = (1 << bits) - 1;
        int result = (int)scratch & mask;
        dropBits(bits);
        return result;
    }

    private void grabBitsIfRequired(int bits) {
        while (scratchBits < bits) {
            scratch |= ((long)get() & 0xff) << scratchBits;
            scratchBits += 8;
        }
    }

    private void dropBits(int bits) {
        scratch >>= bits;
        scratchBits -= bits;
    }
}
