package org.jusecase.bitpack;

import org.junit.Test;
import org.jusecase.bitpack.buffer.BufferBitReader;
import org.jusecase.bitpack.buffer.BufferBitWriter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class BitPackAndUnpackTest {
    BitProtocol protocol = new AbstractBitProtocol();
    BufferBitWriter writer = new BufferBitWriter(protocol, ByteBuffer.allocateDirect(128));
    BufferBitReader reader;


    @Test
    public void string_nonNull() {
        writer.writeStringNonNull(8, "foobar");
        whenBufferIsFlushedAndRead();
        assertThat(reader.readStringNonNull(8)).isEqualTo("foobar");
    }

    @Test
    public void string_nonNull_jp() {
        writer.writeStringNonNull(8, "そいはらせふたいはら");
        whenBufferIsFlushedAndRead();
        assertThat(reader.readStringNonNull(8)).isEqualTo("そいはらせふたいはら");
    }

    @Test
    public void string_tooLong() {
        Throwable throwable = catchThrowable(() -> writer.writeStringNonNull(2, "foobar"));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void string_null() {
        writer.writeStringNullable(8, null);
        writer.writeStringNullable(8, "foo");
        writer.writeStringNullable(8, null);
        writer.writeStringNullable(8, "bar");

        whenBufferIsFlushedAndRead();

        assertThat(reader.readStringNullable(8)).isEqualTo(null);
        assertThat(reader.readStringNullable(8)).isEqualTo("foo");
        assertThat(reader.readStringNullable(8)).isEqualTo(null);
        assertThat(reader.readStringNullable(8)).isEqualTo("bar");
    }

    @Test
    public void uuid() {
        UUID uuid = UUID.randomUUID();
        writer.writeUuidNonNull(uuid);

        whenBufferIsFlushedAndRead();

        assertThat(reader.readUuidNonNull()).isEqualTo(uuid);
    }

    @Test
    public void inetAddress_v4() throws Exception {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        writer.writeInetAddress(address);

        whenBufferIsFlushedAndRead();

        assertThat(reader.readInetAddress()).isEqualTo(address);
    }

    @Test
    public void inetAddress_v6() throws Exception {
        InetAddress address = InetAddress.getByName("::1");
        writer.writeInetAddress(address);

        whenBufferIsFlushedAndRead();

        assertThat(reader.readInetAddress()).isEqualTo(address);
    }

    @Test
    public void inetAddress_null() {
        writer.writeInetAddress(null);

        whenBufferIsFlushedAndRead();

        assertThat(reader.readInetAddress()).isEqualTo(null);
    }

    @Test
    public void inetSocketAddress() throws Exception {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 64312);
        writer.writeInetSocketAddress(address);

        whenBufferIsFlushedAndRead();

        assertThat(reader.readInetSocketAddress()).isEqualTo(address);
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
