package org.jusecase.bitpack.stream;

import org.jusecase.bitpack.BitWriter;
import org.jusecase.bitpack.BitWriterTest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class StreamBitWriterTest extends BitWriterTest {

    ByteArrayOutputStream outputStream ;

    @Override
    protected BitWriter createWriter() {
        outputStream = new ByteArrayOutputStream(128);
        return new StreamBitWriter(protocol, outputStream);
    }

    @Override
    protected byte[] getWrittenData() {
        return outputStream.toByteArray();
    }
}