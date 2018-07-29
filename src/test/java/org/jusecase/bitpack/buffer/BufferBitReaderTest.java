package org.jusecase.bitpack.buffer;

import org.jusecase.bitpack.BitReaderTest;

import java.nio.ByteBuffer;

public class BufferBitReaderTest extends BitReaderTest {
    @Override
    protected void givenBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        buffer.rewind();
        reader = new BufferBitReader(protocol, buffer);
    }
}
