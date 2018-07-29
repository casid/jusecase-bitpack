package org.jusecase.bitpack.buffered;

import org.jusecase.bitpack.BitProtocol;
import org.jusecase.bitpack.BitReader;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferedBitReader implements BitReader {

    private final BitProtocol protocol;
    private final ByteBuffer buffer;

    private long scratch;
    private int scratchBits;

    public BufferedBitReader(BitProtocol protocol, ByteBuffer buffer) {
        this.protocol = protocol;
        this.buffer = buffer;
    }

    public ByteBuffer getBuffer() {
        return buffer;
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
    public byte[] readBytesNonNull() {
        int length = readInt16();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytes[i] = readByte();
        }
        return bytes;
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
    public String readStringNullable() {
        boolean isNonNull = readBoolean();
        if (isNonNull) {
            return readStringNonNull();
        } else {
            return null;
        }
    }

    @Override
    public String readStringNonNull() {
        byte[] bytes = readBytesNonNull();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void grabBitsIfRequired(int bits) {
        while (scratchBits < bits) {
            scratch |= ((long)buffer.get() & 0xff) << scratchBits;
            scratchBits += 8;
        }
    }

    private void dropBits(int bits) {
        scratch >>= bits;
        scratchBits -= bits;
    }
}
