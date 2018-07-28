package org.jusecase.bitpack;

public interface BitSerializer<T> {
    void serialize(BitPacker packer, T object);
    T deserialize(BitUnpacker unpacker);
}
