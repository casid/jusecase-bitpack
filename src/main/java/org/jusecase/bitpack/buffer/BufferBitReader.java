package org.jusecase.bitpack.buffer;

import org.jusecase.bitpack.AbstractBitReader;
import org.jusecase.bitpack.BitProtocol;

import java.nio.ByteBuffer;

public class BufferBitReader extends AbstractBitReader {

    private final ByteBuffer buffer;

    public BufferBitReader(BitProtocol protocol, ByteBuffer buffer) {
        super(protocol);
        this.buffer = buffer;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    protected byte get() {
        return buffer.get();
    }
}
