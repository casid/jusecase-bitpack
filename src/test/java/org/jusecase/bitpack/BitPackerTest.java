package org.jusecase.bitpack;

import org.junit.Test;
import org.jusecase.bitpack.buffered.BufferedBitPacker;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class BitPackerTest {
    private BitProtocol protocol = new BasicBitProtocol();
    BufferedBitPacker bitPacker = new BufferedBitPacker(protocol, ByteBuffer.allocateDirect(128));

    @Test
    public void boolean_false() {
        bitPacker.packBoolean(false);
        thenBitsArePackedAs("00000000");
    }

    @Test
    public void boolean_true() {
        bitPacker.packBoolean(true);
        thenBitsArePackedAs("10000000");
    }

    @Test
    public void boolean_false_true() {
        bitPacker.packBoolean(false);
        bitPacker.packBoolean(true);
        thenBitsArePackedAs("01000000");
    }

    @Test
    public void boolean_33_bits() {
        for (int i = 0; i < 33; ++i) {
            bitPacker.packBoolean(i % 2 == 0);
        }
        thenBitsArePackedAs("10101010 10101010 10101010 10101010 10000000");
    }

    @Test
    public void int12_0() {
        bitPacker.packInt12(0);
        thenBitsArePackedAs("00000000 00000000");
    }

    @Test
    public void int12_1() {
        bitPacker.packInt12(1);
        thenBitsArePackedAs("10000000 00000000");
    }

    @Test
    public void int12_max() {
        bitPacker.packInt12(2047);
        thenBitsArePackedAs("11111111 11100000");
    }

    @Test
    public void int12_min() {
        bitPacker.packInt12(-2048);
        thenBitsArePackedAs("00000000 00010000");
    }

    @Test
    public void int32_0() {
        bitPacker.packInt32(0);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000");
    }

    @Test
    public void int32_1() {
        bitPacker.packInt32(1);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000");
    }

    @Test
    public void int32_max() {
        bitPacker.packInt32(Integer.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111110");
    }

    @Test
    public void int32_minus723955400() {
        bitPacker.packInt32(-723955400);
        thenBitsArePackedAs("00011100 10001010 10011011 00101011");
    }

    @Test
    public void int32_min() {
        bitPacker.packInt32(Integer.MIN_VALUE);
        thenBitsArePackedAs("00000000 00000000 00000000 00000001");
    }

    @Test
    public void int32_int8() {
        bitPacker.packInt32(0xcdcdcdce);
        bitPacker.packInt8(2);
        thenBitsArePackedAs("01110011 10110011 10110011 10110011 01000000");
    }

    @Test
    public void long_0() {
        bitPacker.packLong(0L);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void long_1() {
        bitPacker.packLong(1L);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void long_max() {
        bitPacker.packLong(Long.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111110");
    }

    @Test
    public void long_1_after_boolean() {
        bitPacker.packBoolean(true);
        bitPacker.packLong(1L);
        bitPacker.packBoolean(true);
        thenBitsArePackedAs("11000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 01000000");
    }

    @Test
    public void boolean_false_int32_0() {
        bitPacker.packBoolean(false);
        bitPacker.packInt32(0);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void boolean_true_int32_0() {
        bitPacker.packBoolean(true);
        bitPacker.packInt32(0);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void boolean_true_int32_max() {
        bitPacker.packBoolean(true);
        bitPacker.packInt32(Integer.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111111 00000000");
    }

    @Test
    public void allSupportedTypes() {
        bitPacker.packBoolean(false);
        bitPacker.packBoolean(true);
        bitPacker.packInt12(6);
        bitPacker.packInt32(Integer.MAX_VALUE);
        bitPacker.packInt8(127);
        bitPacker.packBoolean(true); // mark end :-)

        thenBitsArePackedAs("01011000 00000011 11111111 11111111 11111111 11111011 11111010");
    }

    @Test(expected = BufferOverflowException.class)
    public void overflow() {
        bitPacker = new BufferedBitPacker(protocol, ByteBuffer.allocateDirect(4));
        bitPacker.packInt32(1);
        bitPacker.packInt32(1);
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