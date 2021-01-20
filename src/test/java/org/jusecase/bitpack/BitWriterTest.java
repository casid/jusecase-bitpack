package org.jusecase.bitpack;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public abstract class BitWriterTest {
    protected BitProtocol protocol = new AbstractBitProtocol();
    protected BitWriter writer = createWriter();

    protected abstract BitWriter createWriter();

    @Test
    public void boolean_false() {
        writer.writeBoolean(false);
        thenBitsArePackedAs("00000000");
    }

    @Test
    public void boolean_true() {
        writer.writeBoolean(true);
        thenBitsArePackedAs("10000000");
    }

    @Test
    public void boolean_false_true() {
        writer.writeBoolean(false);
        writer.writeBoolean(true);
        thenBitsArePackedAs("01000000");
    }

    @Test
    public void unsignedInt2_0() {
        writer.writeUnsignedInt2(0);
        thenBitsArePackedAs("00000000");
    }

    @Test
    public void unsignedInt2_1() {
        writer.writeUnsignedInt2(1);
        thenBitsArePackedAs("10000000");
    }

    @Test
    public void unsignedInt2_2() {
        writer.writeUnsignedInt2(2);
        thenBitsArePackedAs("01000000");
    }

    @Test
    public void unsignedInt2_3() {
        writer.writeUnsignedInt2(3);
        thenBitsArePackedAs("11000000");
    }

    @Test
    public void unsignedInt2_3_boolean() {
        writer.writeUnsignedInt2(3);
        writer.writeBoolean(true);
        thenBitsArePackedAs("11100000");
    }

    @Test
    public void unsignedInt2_4() {
        Throwable throwable = catchThrowable(() -> writer.writeUnsignedInt2(4));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("2 bit unsigned integer overflow: 4 is greater than max value 3");
    }

    @Test
    public void unsignedInt2_negative() {
        Throwable throwable = catchThrowable(() -> writer.writeUnsignedInt2(-1));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Unsigned integer must not be negative: -1 is an illegal value");
    }

    @Test
    public void unsignedInt6() {
        writer.writeUnsignedInt6(16);
        thenBitsArePackedAs("00001000");
    }

    @Test
    public void boolean_33_bits() {
        for (int i = 0; i < 33; ++i) {
            writer.writeBoolean(i % 2 == 0);
        }
        thenBitsArePackedAs("10101010 10101010 10101010 10101010 10000000");
    }

    @Test
    public void int12_0() {
        writer.writeInt12(0);
        thenBitsArePackedAs("00000000 00000000");
    }

    @Test
    public void int12_1() {
        writer.writeInt12(1);
        thenBitsArePackedAs("10000000 00000000");
    }

    @Test
    public void int12_max() {
        writer.writeInt12(2047);
        thenBitsArePackedAs("11111111 11100000");
    }

    @Test
    public void int12_min() {
        writer.writeInt12(-2048);
        thenBitsArePackedAs("00000000 00010000");
    }

    @Test
    public void int12_overflow() {
        Throwable throwable = catchThrowable(() -> writer.writeInt12(2048));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("12 bit integer overflow: 2048 is greater than max value 2047");
    }

    @Test
    public void int12_underflow() {
        Throwable throwable = catchThrowable(() -> writer.writeInt12(-2049));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("12 bit integer underflow: -2049 is less than min value -2048");
    }

    @Test
    public void int32_0() {
        writer.writeInt32(0);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000");
    }

    @Test
    public void int32_1() {
        writer.writeInt32(1);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000");
    }

    @Test
    public void int32_max() {
        writer.writeInt32(Integer.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111110");
    }

    @Test
    public void int32_minus723955400() {
        writer.writeInt32(-723955400);
        thenBitsArePackedAs("00011100 10001010 10011011 00101011");
    }

    @Test
    public void int32_min() {
        writer.writeInt32(Integer.MIN_VALUE);
        thenBitsArePackedAs("00000000 00000000 00000000 00000001");
    }

    @Test
    public void int32_int8() {
        writer.writeInt32(0xcdcdcdce);
        writer.writeInt8(2);
        thenBitsArePackedAs("01110011 10110011 10110011 10110011 01000000");
    }

    @Test
    public void long_0() {
        writer.writeLong(0L);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void long_1() {
        writer.writeLong(1L);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void long_max() {
        writer.writeLong(Long.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111110");
    }

    @Test
    public void long_1_after_boolean() {
        writer.writeBoolean(true);
        writer.writeLong(1L);
        writer.writeBoolean(true);
        thenBitsArePackedAs("11000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 01000000");
    }

    @Test
    public void boolean_false_int32_0() {
        writer.writeBoolean(false);
        writer.writeInt32(0);
        thenBitsArePackedAs("00000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void boolean_true_int32_0() {
        writer.writeBoolean(true);
        writer.writeInt32(0);
        thenBitsArePackedAs("10000000 00000000 00000000 00000000 00000000");
    }

    @Test
    public void boolean_true_int32_max() {
        writer.writeBoolean(true);
        writer.writeInt32(Integer.MAX_VALUE);
        thenBitsArePackedAs("11111111 11111111 11111111 11111111 00000000");
    }

    @Test
    public void allSupportedTypes() {
        writer.writeBoolean(false);
        writer.writeBoolean(true);
        writer.writeInt12(6);
        writer.writeInt32(Integer.MAX_VALUE);
        writer.writeInt8(127);
        writer.writeBoolean(true); // mark end :-)

        thenBitsArePackedAs("01011000 00000011 11111111 11111111 11111111 11111011 11111010");
    }

    @Test
    public void hex1() {
        writer.writeUnsignedInt(26, 22);
        writer.writeUnsignedInt3(1);
        writer.writeUnsignedInt3(2);

        thenBitsArePackedAsHex("16 00 00 44");
    }

    @Test
    public void hex2() {
        writer.writeUnsignedInt(26, 22);
        writer.writeUnsignedInt3(2);
        writer.writeUnsignedInt3(2);

        thenBitsArePackedAsHex("16 00 00 48");
    }

    protected void thenBitsArePackedAs(String expected) {
        writer.flush();
        byte[] bytes = getWrittenData();

        StringBuilder actual = new StringBuilder();

        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                actual.append(" ");
            }

            byte bits = bytes[i];
            for (int bit = 0; bit < 8; ++bit) {
                actual.append((bits >> bit) & 0x01);
            }
        }

        assertThat(actual.toString()).isEqualTo(expected);
    }

    protected void thenBitsArePackedAsHex(String expected) {
        writer.flush();
        byte[] bytes = getWrittenData();

        StringBuilder actual = new StringBuilder();

        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                actual.append(" ");
            }

            actual.append(Character.forDigit((bytes[i] >> 4) & 0xF, 16));
            actual.append(Character.forDigit((bytes[i] & 0xF), 16));
        }

        assertThat(actual.toString()).isEqualTo(expected);
    }

    protected abstract byte[] getWrittenData();
}