package org.jusecase.bitpack;

import org.junit.Test;
import org.jusecase.bitpack.buffered.BufferedBitPacker;
import org.jusecase.bitpack.buffered.BufferedBitUnpacker;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class BitPackAndUnpackTest {
    BitProtocol protocol = new BasicBitProtocol();
    BufferedBitPacker packer = new BufferedBitPacker(protocol, ByteBuffer.allocateDirect(128));
    BufferedBitUnpacker unpacker;


    @Test
    public void string_nonNull() {
        packer.packStringNonNull("foobar");
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.unpackStringNonNull()).isEqualTo("foobar");
    }

    @Test
    public void string_nonNull_jp() {
        packer.packStringNonNull("そいはらせふたいはら");
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.unpackStringNonNull()).isEqualTo("そいはらせふたいはら");
    }

    @Test
    public void string_null() {
        packer.packStringNullable(null);
        packer.packStringNullable("foo");
        packer.packStringNullable(null);
        packer.packStringNullable("bar");

        whenBufferIsFlushedAndRead();

        assertThat(unpacker.unpackStringNullable()).isEqualTo(null);
        assertThat(unpacker.unpackStringNullable()).isEqualTo("foo");
        assertThat(unpacker.unpackStringNullable()).isEqualTo(null);
        assertThat(unpacker.unpackStringNullable()).isEqualTo("bar");
    }

    @Test
    public void integrationTest1() {
        packer.packInt32(0xcdcdcdce);
        packer.packInt8(2);
        packer.packLong(2935298735982L);

        whenBufferIsFlushedAndRead();
        assertThat(unpacker.unpackInt32()).isEqualTo(0xcdcdcdce);
        assertThat(unpacker.unpackInt8()).isEqualTo(2);
        assertThat(unpacker.unpackLong()).isEqualTo(2935298735982L);
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
        packer.packInt8(0);
        packer.packInt8(1);
        packer.packLong(42);
        packer.packInt8(2);

        whenBufferIsFlushedAndRead();
        assertThat(unpacker.unpackInt8()).isEqualTo(0);
        assertThat(unpacker.unpackInt8()).isEqualTo(1);
        assertThat(unpacker.unpackLong()).isEqualTo(42);
        assertThat(unpacker.unpackInt8()).isEqualTo(2);
    }

    private void thenInt32IsPackedAndUnpackedCorrectly(int value) {
        packer.packInt32(value);
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.unpackInt32()).isEqualTo(value);
    }

    private void thenLongIsPackedAndUnpackedCorrectly(long value) {
        packer.packLong(value);
        whenBufferIsFlushedAndRead();
        assertThat(unpacker.unpackLong()).isEqualTo(value);
    }

    private void whenBufferIsFlushedAndRead() {
        packer.flush();
        packer.getBuffer().rewind();
        unpacker = new BufferedBitUnpacker(protocol, packer.getBuffer());
    }

}
