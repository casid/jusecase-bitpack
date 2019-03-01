package org.jusecase.bitpack;

import java.nio.charset.StandardCharsets;

public abstract class AbstractBitWriter implements BitWriter {
    private static final int MAX_WORD_BITS = 32;
    private static final int MAX_WORD_BYTES = MAX_WORD_BITS / 8;

    private final BitProtocol protocol;

    private long scratch;
    private int scratchBits;
    private int byteCount;
    private byte[] scratchOutput = new byte[MAX_WORD_BYTES];

    protected AbstractBitWriter(BitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void flush() {
        flushScratch();
    }

    @Override
    public BitProtocol getProtocol() {
        return protocol;
    }

    @Override
    public void writeBoolean(boolean value) {
        scratch |= (value ? 1L : 0L) << scratchBits;
        flushScratchIfRequired(1);
    }

    @Override
    public void writeByte(byte value) {
        scratch |= (value & 0x00000000000000ffL) << scratchBits;
        flushScratchIfRequired(8);
    }

    @Override
    public void writeBytesNonNull(int lengthBits, byte[] values) {
        writeUnsignedInt(lengthBits, values.length);
        for (byte value : values) {
            writeByte(value);
        }
    }

    @Override
    public void writeUnsignedInt2(int value) {
        writeUnsignedInt(2, value);
    }

    @Override
    public void writeUnsignedInt3(int value) {
        writeUnsignedInt(3, value);
    }

    @Override
    public void writeUnsignedInt4(int value) {
        writeUnsignedInt(4, value);
    }

    @Override
    public void writeUnsignedInt5(int value) {
        writeUnsignedInt(5, value);
    }

    @Override
    public void writeUnsignedInt6(int value) {
        writeUnsignedInt(6, value);
    }

    @Override
    public void writeUnsignedInt7(int value) {
        writeUnsignedInt(7, value);
    }

    @Override
    public void writeUnsignedInt8(int value) {
        writeUnsignedInt(8, value);
    }

    @Override
    public void writeUnsignedInt9(int value) {
        writeUnsignedInt(9, value);
    }

    @Override
    public void writeUnsignedInt10(int value) {
        writeUnsignedInt(10, value);
    }

    @Override
    public void writeUnsignedInt11(int value) {
        writeUnsignedInt(11, value);
    }

    @Override
    public void writeUnsignedInt12(int value) {
        writeUnsignedInt(12, value);
    }

    @Override
    public void writeUnsignedInt13(int value) {
        writeUnsignedInt(13, value);
    }

    @Override
    public void writeUnsignedInt14(int value) {
        writeUnsignedInt(14, value);
    }

    @Override
    public void writeUnsignedInt15(int value) {
        writeUnsignedInt(15, value);
    }

    @Override
    public void writeUnsignedInt16(int value) {
        writeUnsignedInt(16, value);
    }

    @Override
    public void writeInt8(int value) {
        writeInt(8, value);
    }

    @Override
    public void writeInt12(int value) {
        writeInt(12, value);
    }

    @Override
    public void writeInt16(int value) {
        writeInt(16, value);
    }

    @Override
    public void writeInt32(int value) {
        scratch |= (value & 0x00000000ffffffffL) << scratchBits;
        flushScratchIfRequired(32);
    }

    @Override
    public void writeLong(long value) {
        writeInt32((int) value);
        writeInt32((int) (value >> 32));
    }

    @Override
    public void writeStringNullable(int lengthBits, String value) {
        if (value == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeStringNonNull(lengthBits, value);
        }
    }

    @Override
    public void writeStringNonNull(int lengthBits, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeBytesNonNull(lengthBits, bytes);
    }

    public int getByteCount() {
        return byteCount;
    }

    public void reset() {
        resetUnderlyingData();
        scratch = 0L;
        scratchBits = 0;
        byteCount = 0;
    }

    @Override
    public void writeUnsignedInt(int bits, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Unsigned integer must not be negative: " + value + " is an illegal value");
        }

        if (value >= 1 << bits) {
            throw new IllegalArgumentException(bits + " bit unsigned integer overflow: " + value + " is greater than max value " + ((1 << bits) - 1));
        }

        long mask = (1L << bits) - 1L;
        scratch |= (value & mask) << scratchBits;
        flushScratchIfRequired(bits);
    }

    private void writeInt(int bits, int value) {
        if (value < -(1 << (bits - 1))) {
            throw new IllegalArgumentException(bits + " bit integer underflow: " + value + " is less than min value " + (-(1 << (bits - 1))));
        }

        if (value >= 1 << (bits - 1)) {
            throw new IllegalArgumentException(bits + " bit integer overflow: " + value + " is greater than max value " + ((1 << (bits - 1)) - 1));
        }

        long mask = (1L << bits) - 1L;
        scratch |= (value & mask) << scratchBits;
        flushScratchIfRequired(bits);
    }

    private void flushScratchIfRequired(int bits) {
        if ((scratchBits += bits) >= MAX_WORD_BITS) {
            flushScratch();
            scratchBits -= MAX_WORD_BITS;
        }
    }

    private void flushScratch() {
        int newBytes = Math.min(MAX_WORD_BYTES, scratchBits / 8 + (scratchBits % 8 > 0 ? 1 : 0));
        for (int i = 0; i < newBytes; ++i) {
            scratchOutput[i] = (byte) ((scratch >> 8 * i) & 0xff);
        }
        put(scratchOutput, newBytes);

        scratch >>= MAX_WORD_BITS;
        byteCount += newBytes;
    }

    protected abstract void put(byte[] bytes, int count);

    protected abstract void resetUnderlyingData();
}
