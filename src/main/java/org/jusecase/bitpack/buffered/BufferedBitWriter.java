package org.jusecase.bitpack.buffered;

import org.jusecase.bitpack.BitWriter;
import org.jusecase.bitpack.BitProtocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferedBitWriter implements BitWriter {

    private static final int maxWordBits = 32;
    private static final int maxWordBytes = maxWordBits / 8;
    private final BitProtocol protocol;
    private final ByteBuffer buffer;
    private long scratch;
    private int scratchBits;
    private int byteCount;

    public BufferedBitWriter(BitProtocol protocol, ByteBuffer buffer) {
        this.protocol = protocol;
        this.buffer = buffer;
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

    private void flushScratchIfRequired(int bits) {
        if ((scratchBits += bits) >= maxWordBits) {
            flushScratch();
            scratchBits -= maxWordBits;
        }
    }

    private void flushScratch() {
        int newBytes = Math.min(maxWordBytes, scratchBits / 8 + (scratchBits % 8 > 0 ? 1 : 0));
        for (int i = 0; i < newBytes; ++i) {
            buffer.put((byte) ((scratch >> 8 * i) & 0xff));
        }

        scratch >>= maxWordBits;
        byteCount += newBytes;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getByteCount() {
        return byteCount;
    }

    public void reset() {
        buffer.clear();
        scratch = 0L;
        scratchBits = 0;
        byteCount = 0;
    }
}
