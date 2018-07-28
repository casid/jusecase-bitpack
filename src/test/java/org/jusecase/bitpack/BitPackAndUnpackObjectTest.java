package org.jusecase.bitpack;

import org.junit.Before;
import org.jusecase.bitpack.buffered.BufferedBitPacker;
import org.jusecase.bitpack.buffered.BufferedBitUnpacker;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BitPackAndUnpackObjectTest<T> {
    protected T object;
    protected T deserializedObject;

    protected BitProtocol protocol;
    protected BitPacker writer;
    protected BitUnpacker reader;

    private ByteBuffer buffer;

    @Before
    public void setUp() {
        protocol = new BasicBitProtocol();

        buffer = ByteBuffer.allocateDirect(1500);
        writer = new BufferedBitPacker(protocol, buffer);
        reader = new BufferedBitUnpacker(protocol, buffer);

        object = newObject();
    }

    protected T newObject() {
        try {
            return getObjectClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getObjectClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    protected void whenEntityIsSerializedAndDeserialized() {
        writer.packObjectNonNull(object);
        writer.flush();

        buffer.rewind();
        deserializedObject = (T)reader.unpackObjectNonNull(object.getClass());
    }

    protected void thenEntitySerializationWorks() {
        whenEntityIsSerializedAndDeserialized();
        assertThat(deserializedObject).isEqualToComparingFieldByField(object);
    }
}
