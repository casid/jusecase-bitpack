package org.jusecase.bitpack;

import org.junit.Test;
import org.jusecase.bitpack.buffered.BufferedBitWriter;
import org.jusecase.bitpack.buffered.BufferedBitReader;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class BitPackAndUnpackTest {
    BitProtocol protocol = new BasicBitProtocol();
    BufferedBitWriter packer = new BufferedBitWriter(protocol, ByteBuffer.allocateDirect(128));
    BufferedBitReader unpacker;


    @Test
    public void string_nonNull() {
        packer.writeStringNonNull("foobar");
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.readStringNonNull()).isEqualTo("foobar");
    }

    @Test
    public void string_nonNull_jp() {
        packer.writeStringNonNull("そいはらせふたいはら");
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.readStringNonNull()).isEqualTo("そいはらせふたいはら");
    }

    @Test
    public void string_null() {
        packer.writeStringNullable(null);
        packer.writeStringNullable("foo");
        packer.writeStringNullable(null);
        packer.writeStringNullable("bar");

        whenBufferIsFlushedAndRead();

        assertThat(unpacker.readStringNullable()).isEqualTo(null);
        assertThat(unpacker.readStringNullable()).isEqualTo("foo");
        assertThat(unpacker.readStringNullable()).isEqualTo(null);
        assertThat(unpacker.readStringNullable()).isEqualTo("bar");
    }

    @Test
    public void integrationTest1() {
        packer.writeInt32(0xcdcdcdce);
        packer.writeInt8(2);
        packer.writeLong(2935298735982L);

        whenBufferIsFlushedAndRead();
        assertThat(unpacker.readInt32()).isEqualTo(0xcdcdcdce);
        assertThat(unpacker.readInt8()).isEqualTo(2);
        assertThat(unpacker.readLong()).isEqualTo(2935298735982L);
    }

    @Test
    public void integrationTest2() {
        Random fixedRandom = new Random(0);
        for (int i = 0; i < 1024; ++i) {
            packer.reset();
            thenInt32IsPackedAndUnpackedCorrectly(fixedRandom.nextInt());
        }
    }

    @Test
    public void integrationTest3() {
        Random fixedRandom = new Random(0);
        for (int i = 0; i < 1024; ++i) {
            packer.reset();
            thenLongIsPackedAndUnpackedCorrectly(fixedRandom.nextLong());
        }
    }

    @Test
    public void integrationTest4() {
        packer.writeInt8(0);
        packer.writeInt8(1);
        packer.writeLong(42);
        packer.writeInt8(2);

        whenBufferIsFlushedAndRead();
        assertThat(unpacker.readInt8()).isEqualTo(0);
        assertThat(unpacker.readInt8()).isEqualTo(1);
        assertThat(unpacker.readLong()).isEqualTo(42);
        assertThat(unpacker.readInt8()).isEqualTo(2);
    }

    private void thenInt32IsPackedAndUnpackedCorrectly(int value) {
        packer.writeInt32(value);
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.readInt32()).isEqualTo(value);
    }

    private void thenLongIsPackedAndUnpackedCorrectly(long value) {
        packer.writeLong(value);
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.readLong()).isEqualTo(value);
    }

    private void whenBufferIsFlushedAndRead() {
        packer.flush();
        packer.getBuffer().rewind();
        unpacker = new BufferedBitReader(protocol, packer.getBuffer());
    }

}
