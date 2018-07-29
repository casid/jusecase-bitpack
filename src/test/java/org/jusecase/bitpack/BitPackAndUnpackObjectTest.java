package org.jusecase.bitpack;

import org.junit.Before;
import org.jusecase.bitpack.buffer.BufferBitWriter;
import org.jusecase.bitpack.buffer.BufferBitReader;

import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BitPackAndUnpackObjectTest<T> {
    protected T object;
    protected T deserializedObject;

    protected BitProtocol protocol;
    protected BitWriter writer;
    protected BitReader reader;

    private ByteBuffer buffer;

    @Before
    public void setUp() {
        protocol = new AbstractBitProtocol();

        buffer = ByteBuffer.allocateDirect(1500);
        writer = new BufferBitWriter(protocol, buffer);
        reader = new BufferBitReader(protocol, buffer);

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
        writer.writeObjectNonNull(object);
        writer.flush();

        buffer.rewind();
        deserializedObject = (T)reader.readObjectNonNull(object.getClass());
    }

    protected void thenEntitySerializationWorks() {
        whenEntityIsSerializedAndDeserialized();
        assertThat(deserializedObject).isEqualToComparingFieldByField(object);
    }
}
