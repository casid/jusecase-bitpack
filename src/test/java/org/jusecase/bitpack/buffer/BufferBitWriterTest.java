package org.jusecase.bitpack.buffer;

import org.junit.Test;
import org.jusecase.bitpack.BitWriter;
import org.jusecase.bitpack.BitWriterTest;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class BufferBitWriterTest extends BitWriterTest {
    @Override
    protected BitWriter createWriter() {
        return new BufferBitWriter(protocol, ByteBuffer.allocateDirect(128));
    }

    @Override
    protected byte[] getWrittenData() {
        ByteBuffer output = ((BufferBitWriter)writer).getBuffer();
        output.rewind();

        byte[] bytes = new byte[((BufferBitWriter)writer).getByteCount()];
        output.get(bytes);

        return bytes;
    }

    @Test(expected = BufferOverflowException.class)
    public void overflow() {
        writer = new BufferBitWriter(protocol, ByteBuffer.allocateDirect(4));
        writer.writeInt32(1);
        writer.writeInt32(1);
    }
}
