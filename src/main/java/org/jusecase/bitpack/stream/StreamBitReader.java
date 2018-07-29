package org.jusecase.bitpack.stream;

import org.jusecase.bitpack.AbstractBitReader;
import org.jusecase.bitpack.BitProtocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class StreamBitReader extends AbstractBitReader {

    private final InputStream inputStream;

    private byte[] scratchBuffer = new byte[1];

    public StreamBitReader(BitProtocol protocol, InputStream inputStream) {
        super(protocol);
        this.inputStream = inputStream;
    }

    @Override
    protected byte get() {
        try {
            int result = inputStream.read(scratchBuffer);
            if (result < 0) {
                throw new EOFException("End of input stream reached");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from input stream", e);
        }
        return scratchBuffer[0];
    }
}
