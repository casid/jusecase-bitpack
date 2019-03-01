package org.jusecase.bitpack;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class BitTypesTest {
    BitTypes types = new BitTypes();

    @Test
    public void oneClass() {
        types.register(1, BigDecimal.class);
        assertThat(types.getRequiredBits()).isEqualTo(1);
    }

    @Test
    public void oneClass_bigId() {
        types.register(2, BigDecimal.class);
        assertThat(types.getRequiredBits()).isEqualTo(2);
    }

    @Test
    public void threeClasses() {
        types.register(1, BigDecimal.class);
        types.register(2, String.class);
        types.register(3, Integer.class);

        assertThat(types.getRequiredBits()).isEqualTo(2);
    }

    @Test
    public void fourClasses() {
        types.register(1, BigDecimal.class);
        types.register(2, String.class);
        types.register(3, Integer.class);
        types.register(4, Double.class);

        assertThat(types.getRequiredBits()).isEqualTo(3);
    }

    @Test
    public void fiveClasses() {
        types.register(1, BigDecimal.class);
        types.register(2, String.class);
        types.register(3, Integer.class);
        types.register(4, Double.class);
        types.register(5, Float.class);

        assertThat(types.getRequiredBits()).isEqualTo(3);
    }

    @Test
    public void eightClasses() {
        types.register(1, BigDecimal.class);
        types.register(2, String.class);
        types.register(3, Integer.class);
        types.register(4, Double.class);
        types.register(5, Float.class);
        types.register(6, Boolean.class);
        types.register(7, StringBuilder.class);
        types.register(8, StringBuffer.class);

        assertThat(types.getRequiredBits()).isEqualTo(4);
    }
}