package org.jusecase.bitpack;

import org.junit.Test;
import org.jusecase.bitpack.buffered.BufferedBitWriter;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class BitWriterTest {
    private BitProtocol protocol = new BasicBitProtocol();
    BufferedBitWriter bitPacker = new BufferedBitWriter(protocol, ByteBuffer.allocateDirect(128));

    @Test
    public void boolean_false() {
        bitPacker.writeBoolean(false);
        thenBitsArePackedAs("00000000");
    }

    @Test
    public void boolean_true() {
        bitPacker.writeBoolean(true);
        thenBitsArePackedAs("10000000");
    }

    @Test
    public void boolean_false_true() {
        bitPacker.writeBoolean(false);
        bitPacker.writeBoolean(true);
        thenBitsArePackedAs("01000000");
    }

    @Test
    public void boolean_33_bits() {
        for (int i = 0; i < 33; ++i) {
            bitPacker.writeBoolean(i % 2 == 0);
        }
        thenBitsArePackedAs("10101010 10101010 10101010 10101010 10000000");
    }

    @Test
    public void int12_0() {
        bitPacker.writeInt12(0);
        thenBitsArePackedAs("00000000 00000000");
    }

    @Test
    public void int12_1() {
        bitPacker.writeInt12(1);
        thenBitsArePackedAs("10000000 00000000");
    }

    @Test
    public void int12_max() {
        bitPacker.writeInt12(2047);
        thenBitsArePackedAs("11111111 11100000");
    }

    @Test
    public void int12_min() {
        bitPacker.writeInt12(-2048);
        thenBitsArePackedAs("00000000 00010000");
    }

    @Test
    public void int32_0() {
        bitPacker.writeInt32(0);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000");
    }

    @Test
    public void int32_1() {
        bitPacker.writeInt32(1);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000");
    }

    @Test
    public void int32_max() {
        bitPacker.writeInt32(Integer.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111110");
    }

    @Test
    public void int32_minus723955400() {
        bitPacker.writeInt32(-723955400);
        thenBitsArePackedAs("00011100 10001010 10011011 00101011");
    }

    @Test
    public void int32_min() {
        bitPacker.writeInt32(Integer.MIN_VALUE);
        thenBitsArePackedAs("00000000 00000000 00000000 00000001");
    }

    @Test
    public void int32_int8() {
        bitPacker.writeInt32(0xcdcdcdce);
        bitPacker.writeInt8(2);
        thenBitsArePackedAs("01110011 10110011 10110011 10110011 01000000");
    }

    @Test
    public void long_0() {
        bitPacker.writeLong(0L);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void long_1() {
        bitPacker.writeLong(1L);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void long_max() {
        bitPacker.writeLong(Long.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111110");
    }

    @Test
    public void long_1_after_boolean() {
        bitPacker.writeBoolean(true);
        bitPacker.writeLong(1L);
        bitPacker.writeBoolean(true);
        thenBitsArePackedAs("11000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 01000000");
    }

    @Test
    public void boolean_false_int32_0() {
        bitPacker.writeBoolean(false);
        bitPacker.writeInt32(0);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void boolean_true_int32_0() {
        bitPacker.writeBoolean(true);
        bitPacker.writeInt32(0);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void boolean_true_int32_max() {
        bitPacker.writeBoolean(true);
        bitPacker.writeInt32(Integer.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111111 00000000");
    }

    @Test
    public void allSupportedTypes() {
        bitPacker.writeBoolean(false);
        bitPacker.writeBoolean(true);
        bitPacker.writeInt12(6);
        bitPacker.writeInt32(Integer.MAX_VALUE);
        bitPacker.writeInt8(127);
        bitPacker.writeBoolean(true); // mark end :-)

        thenBitsArePackedAs("01011000 00000011 11111111 11111111 11111111 11111011 11111010");
    }

    @Test(expected = BufferOverflowException.class)
    public void overflow() {
        bitPacker = new BufferedBitWriter(protocol, ByteBuffer.allocateDirect(4));
        bitPacker.writeInt32(1);
        bitPacker.writeInt32(1);
    }

    private void thenBitsArePackedAs(String expected) {
        bitPacker.flush();

        ByteBuffer output = bitPacker.getBuffer();
        output.rewind();

        StringBuilder actual = new StringBuilder();

        int bytes = bitPacker.getByteCount();
        for (int i = 0; i < bytes; ++i) {
            if (i > 0) {
                actual.append(" ");
            }

            byte bits = output.get();
            for (int bit = 0; bit < 8; ++bit) {
                actual.append((bits >> bit) & 0x01);
            }
        }

        assertThat(actual.toString()).isEqualTo(expected);
    }
}