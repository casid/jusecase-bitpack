package org.jusecase.bitpack.buffered;

import org.jusecase.bitpack.BitPacker;
import org.jusecase.bitpack.BitProtocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferedBitPacker implements BitPacker {

    private static final int maxWordBits = 32;
    private static final int maxWordBytes = maxWordBits / 8;
    private final BitProtocol protocol;
    private final ByteBuffer buffer;
    private long scratch;
    private int scratchBits;
    private int byteCount;

    public BufferedBitPacker(BitProtocol protocol, ByteBuffer buffer) {
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
    public void packBoolean(boolean value) {
        scratch |= (value ? 1L : 0L) << scratchBits;
        flushScratchIfRequired(1);
    }

    @Override
    public void packByte(byte value) {
        scratch |= (value & 0x00000000000000ffL) << scratchBits;
        flushScratchIfRequired(8);
    }

    @Override
    public void packBytesNonNull(byte[] values) {
        packInt16(values.length);
        for (byte value : values) {
            packByte(value);
        }
    }

    @Override
    public void packInt8(int value) {
        scratch |= (value & 0x00000000000000ffL) << scratchBits;
        flushScratchIfRequired(8);
    }

    @Override
    public void packInt12(int value) {
        scratch |= (value & 0x0000000000000fffL) << scratchBits;
        flushScratchIfRequired(12);
    }

    @Override
    public void packInt16(int value) {
        scratch |= (value & 0x000000000000ffffL) << scratchBits;
        flushScratchIfRequired(16);
    }

    @Override
    public void packInt32(int value) {
        scratch |= (value & 0x00000000ffffffffL) << scratchBits;
        flushScratchIfRequired(32);
    }

    @Override
    public void packLong(long value) {
        packInt32((int) value);
        packInt32((int) (value >> 32));
    }

    @Override
    public void packStringNullable(String value) {
        if (value == null) {
            packBoolean(false);
        } else {
            packBoolean(true);
            packStringNonNull(value);
        }
    }

    @Override
    public void packStringNonNull(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        packBytesNonNull(bytes);
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
