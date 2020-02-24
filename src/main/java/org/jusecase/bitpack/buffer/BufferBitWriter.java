package org.jusecase.bitpack.buffer;

import org.jusecase.bitpack.AbstractBitWriter;
import org.jusecase.bitpack.BitProtocol;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class BufferBitWriter extends AbstractBitWriter {

    private final ByteBuffer buffer;

    public BufferBitWriter(BitProtocol protocol, ByteBuffer buffer) {
        super(protocol);
        this.buffer = buffer;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    protected void put(byte[] bytes, int count) {
        buffer.put(bytes, 0, count);
    }

    @Override
    protected void resetUnderlyingData() {
        //noinspection RedundantCast to be able to compile with Java 9+
        ((Buffer) buffer).clear();
    }
}
