package org.jusecase.bitpack.stream;

import org.jusecase.bitpack.BitReaderTest;

import java.io.ByteArrayInputStream;

public class StreamBitReaderTest extends BitReaderTest {
    @Override
    protected void givenBytes(byte[] bytes) {
        reader = new StreamBitReader(protocol, new ByteArrayInputStream(bytes));
    }
}
