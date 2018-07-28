package org.jusecase.bitpack;

public interface BitProtocol {
    <T> void register(int objectType, BitSerializer<T> serializer);

    <T> void register(BitSerializer<T> serializer);

    BitSerializer<?> getSerializer(Object object);

    BitSerializer<?> getSerializer(Class<?> objectClass);

    BitTypes getBitTypes();
}
