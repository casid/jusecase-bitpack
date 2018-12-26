package org.jusecase.bitpack;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BitReaderTest {
    protected BitProtocol protocol = new AbstractBitProtocol();
    protected BitReader reader;

    @Test
    public void boolean_false() {
        givenBits("00000000");
        assertThat(reader.readBoolean()).isEqualTo(false);
    }

    @Test
    public void boolean_true() {
        givenBits("10000000");
        assertThat(reader.readBoolean()).isEqualTo(true);
    }

    @Test
    public void boolean_false_true() {
        givenBits("01000000");
        assertThat(reader.readBoolean()).isEqualTo(false);
        assertThat(reader.readBoolean()).isEqualTo(true);
    }

    @Test
    public void boolean_33_bits() {
        givenBits("10101010 10101010 10101010 10101010 10000000");
        for (int i = 0; i < 33; ++i) {
            assertThat(reader.readBoolean()).isEqualTo(i % 2 == 0);
        }
    }

    @Test
    public void unsignedInt2_0() {
        givenBits("00000000");
        assertThat(reader.readUnsignedInt2()).isEqualTo(0);
    }

    @Test
    public void unsignedInt2_1() {
        givenBits("10000000");
        assertThat(reader.readUnsignedInt2()).isEqualTo(1);
    }

    @Test
    public void unsignedInt2_2() {
        givenBits("01000000");
        assertThat(reader.readUnsignedInt2()).isEqualTo(2);
    }

    @Test
    public void unsignedInt2_3() {
        givenBits("11000000");
        assertThat(reader.readUnsignedInt2()).isEqualTo(3);
    }

    @Test
    public void unsignedInt6() {
        givenBits("00001000");
        assertThat(reader.readUnsignedInt6()).isEqualTo(16);
    }

    @Test
    public void int8_minus3() {
        givenBits("10111111");
        assertThat(reader.readInt8()).isEqualTo(-3);
    }

    @Test
    public void int12_0() {
        givenBits("00000000 00000000");
        assertThat(reader.readInt12()).isEqualTo(0);
    }

    @Test
    public void int12_1() {
        givenBits("10000000 00000000");
        assertThat(reader.readInt12()).isEqualTo(1);
    }

    @Test
    public void int12_max() {
        givenBits("11111111 11100000");
        assertThat(reader.readInt12()).isEqualTo(2047);
    }

    @Test
    public void int12_min() {
        givenBits("00000000 00010000");
        assertThat(reader.readInt12()).isEqualTo(-2048);
    }

    @Test
    public void int12_minus3() {
        givenBits("10111111 11110000");
        assertThat(reader.readInt12()).isEqualTo(-3);
    }

    @Test
    public void boolean_false_int12_minus3() {
        givenBits("01011111 11111000");
        assertThat(reader.readBoolean()).isEqualTo(false);
        assertThat(reader.readInt12()).isEqualTo(-3);
    }

    @Test
    public void int32_0() {
        givenBits("00000000 00000000 00000000 00000000");
        assertThat(reader.readInt32()).isEqualTo(0);
    }

    @Test
    public void int32_1() {
        givenBits("10000000 00000000 00000000 00000000");
        assertThat(reader.readInt32()).isEqualTo(1);
    }

    @Test
    public void int32_max() {
        givenBits("11111111 11111111 11111111 11111110");
        assertThat(reader.readInt32()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void int32_min() {
        givenBits("00000000 00000000 00000000 00000001");
        assertThat(reader.readInt32()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void int32_minus723955400() {
        givenBits("00011100 10001010 10011011 00101011");
        assertThat(reader.readInt32()).isEqualTo(-723955400);
    }

    @Test
    public void boolean_false_int32_0() {
        givenBits("00000000 00000000 00000000 00000000 00000000");
        assertThat(reader.readBoolean()).isEqualTo(false);
        assertThat(reader.readInt32()).isEqualTo(0);
    }

    @Test
    public void boolean_true_int32_0() {
        givenBits("10000000 00000000 00000000 00000000 00000000");
        assertThat(reader.readBoolean()).isEqualTo(true);
        assertThat(reader.readInt32()).isEqualTo(0);
    }

    @Test
    public void long_0() {
        givenBits("00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
        assertThat(reader.readLong()).isEqualTo(0L);
    }

    @Test
    public void long_1() {
        givenBits("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
        assertThat(reader.readLong()).isEqualTo(1L);
    }

    @Test
    public void long_max() {
        givenBits("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111110");
        assertThat(reader.readLong()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void long_max_after_boolean() {
        givenBits("01111111 11111111 11111111 11111111 11111111 11111111 11111111 11111111 00000000");
        assertThat(reader.readBoolean()).isFalse();
        assertThat(reader.readLong()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void long_1_after_boolean() {
        givenBits("11000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 01000000");
        assertThat(reader.readBoolean()).isTrue();
        assertThat(reader.readLong()).isEqualTo(1L);
        assertThat(reader.readBoolean()).isTrue();
    }

    @Test
    public void allSupportedTypes() {
        givenBits("01011000 00000011 11111111 11111111 11111111 11111011 11111010");

        assertThat(reader.readBoolean()).isEqualTo(false);
        assertThat(reader.readBoolean()).isEqualTo(true);
        assertThat(reader.readInt12()).isEqualTo(6);
        assertThat(reader.readInt32()).isEqualTo(Integer.MAX_VALUE);
        assertThat(reader.readInt8()).isEqualTo(127);
        assertThat(reader.readBoolean()).isEqualTo(true); // mark end :-)
    }

    protected void givenBits(String bitString) {
        int byteCount = (bitString.length() + 1) / 9;
        byte[] bytes = new byte[byteCount];

        for (int i = 0; i < byteCount; ++i) {
            byte bits = 0;
            for (int bit = 0; bit < 8; ++bit) {
                bits |= (bitString.charAt(9 * i + bit) == '1' ? 1 : 0) << bit;
            }
            bytes[i] = bits;
        }

        givenBytes(bytes);
    }

    protected abstract void givenBytes(byte[] bytes);
}