package org.jusecase.bitpack.stream;

import org.jusecase.bitpack.AbstractBitWriter;
import org.jusecase.bitpack.BitProtocol;

import java.io.IOException;
import java.io.OutputStream;

public class StreamBitWriter extends AbstractBitWriter implements AutoCloseable {

    private final OutputStream outputStream;

    public StreamBitWriter(BitProtocol protocol, OutputStream outputStream) {
        super(protocol);
        this.outputStream = outputStream;
    }

    @Override
    protected void put(byte[] bytes, int count) {
        try {
            outputStream.write(bytes, 0, count);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to output stream", e);
        }
    }

    @Override
    protected void resetUnderlyingData() {
        throw new UnsupportedOperationException("Resetting an output stream is not supported by default");
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
