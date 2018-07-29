package org.jusecase.bitpack.buffer;

import org.jusecase.bitpack.AbstractBitReader;
import org.jusecase.bitpack.BitProtocol;
import org.jusecase.bitpack.BitReader;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
