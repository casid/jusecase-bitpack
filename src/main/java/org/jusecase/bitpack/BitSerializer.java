package org.jusecase.bitpack;

public interface BitSerializer<T> {
    void serialize(BitWriter writer, T object);
    T deserialize(BitReader reader);
}
