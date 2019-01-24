package org.jusecase.bitpack;

public interface BitSerializer<T> {
    T createObject();
    void serialize(BitWriter writer, T object);
    void deserialize(BitReader reader, T object);

    default T deserialize(BitReader reader) {
        T object = createObject();
        deserialize(reader, object);
        return object;
    }
}
