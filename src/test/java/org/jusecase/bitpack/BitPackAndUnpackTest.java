package org.jusecase.bitpack;

import org.junit.Test;
import org.jusecase.bitpack.buffer.BufferBitWriter;
import org.jusecase.bitpack.buffer.BufferBitReader;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class BitPackAndUnpackTest {
    BitProtocol protocol = new AbstractBitProtocol();
    BufferBitWriter writer = new BufferBitWriter(protocol, ByteBuffer.allocateDirect(128));
    BufferBitReader reader;


    @Test
    public void string_nonNull() {
        writer.writeStringNonNull("foobar");
        whenBufferIsFlushedAndRead();
        assertThat(reader.readStringNonNull()).isEqualTo("foobar");
    }

    @Test
    public void string_nonNull_jp() {
        writer.writeStringNonNull("そいはらせふたいはら");
        whenBufferIsFlushedAndRead();
        assertThat(reader.readStringNonNull()).isEqualTo("そいはらせふたいはら");
    }

    @Test
    public void string_null() {
        writer.writeStringNullable(null);
        writer.writeStringNullable("foo");
        writer.writeStringNullable(null);
        writer.writeStringNullable("bar");

        whenBufferIsFlushedAndRead();

        assertThat(reader.readStringNullable()).isEqualTo(null);
        assertThat(reader.readStringNullable()).isEqualTo("foo");
        assertThat(reader.readStringNullable()).isEqualTo(null);
        assertThat(reader.readStringNullable()).isEqualTo("bar");
    }

    @Test
    public void integrationTest1() {
        writer.writeInt32(0xcdcdcdce);
        writer.writeInt8(2);
        writer.writeLong(2935298735982L);

        whenBufferIsFlushedAndRead();
        assertThat(reader.readInt32()).isEqualTo(0xcdcdcdce);
        assertThat(reader.readInt8()).isEqualTo(2);
        assertThat(reader.readLong()).isEqualTo(2935298735982L);
    }

    @Test
    public void integrationTest2() {
        Random fixedRandom = new Random(0);
        for (int i = 0; i < 1024; ++i) {
            writer.reset();
            thenInt32IsPackedAndUnpackedCorrectly(fixedRandom.nextInt());
        }
    }

    @Test
    public void integrationTest3() {
        Random fixedRandom = new Random(0);
        for (int i = 0; i < 1024; ++i) {
            writer.reset();
            thenLongIsPackedAndUnpackedCorrectly(fixedRandom.nextLong());
        }
    }

    @Test
    public void integrationTest4() {
        writer.writeInt8(0);
        writer.writeInt8(1);
        writer.writeLong(42);
        writer.writeInt8(2);

        whenBufferIsFlushedAndRead();
        assertThat(reader.readInt8()).isEqualTo(0);
        assertThat(reader.readInt8()).isEqualTo(1);
        assertThat(reader.readLong()).isEqualTo(42);
        assertThat(reader.readInt8()).isEqualTo(2);
    }

    private void thenInt32IsPackedAndUnpackedCorrectly(int value) {
        writer.writeInt32(value);
        whenBufferIsFlushedAndRead();
        assertThat(reader.readInt32()).isEqualTo(value);
    }

    private void thenLongIsPackedAndUnpackedCorrectly(long value) {
        writer.writeLong(value);
        whenBufferIsFlushedAndRead();
        assertThat(reader.readLong()).isEqualTo(value);
    }

    private void whenBufferIsFlushedAndRead() {
        writer.flush();
        writer.getBuffer().rewind();
        reader = new BufferBitReader(protocol, writer.getBuffer());
    }

}
