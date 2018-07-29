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
    public void writeBytesNonNull(byte[] values) {
        writeInt16(values.length);
        for (byte value : values) {
            writeByte(value);
        }
    }

    @Override
    public void writeInt8(int value) {
        scratch |= (value & 0x00000000000000ffL) << scratchBits;
        flushScratchIfRequired(8);
    }

    @Override
    public void writeInt12(int value) {
        scratch |= (value & 0x0000000000000fffL) << scratchBits;
        flushScratchIfRequired(12);
    }

    @Override
    public void writeInt16(int value) {
        scratch |= (value & 0x000000000000ffffL) << scratchBits;
        flushScratchIfRequired(16);
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
    public void writeStringNullable(String value) {
        if (value == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeStringNonNull(value);
        }
    }

    @Override
    public void writeStringNonNull(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeBytesNonNull(bytes);
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
