package org.jusecase.bitpack;

import java.util.HashMap;
import java.util.Map;

public class AbstractBitProtocol implements BitProtocol {
    private final Map<Class<?>, BitSerializer<?>> classToSerializer = new HashMap<>();
    private final BitTypes bitTypes = new BitTypes();

    private int nextType = 1;

    @Override
    public <T> void register(int objectType, BitSerializer<T> serializer) {
        Class<T> objectClass = serializer.getObjectClass();
        classToSerializer.put(objectClass, serializer);
        bitTypes.register(objectType, objectClass);

        nextType = objectType + 1;
    }

    @Override
    public <T> void register(BitSerializer<T> serializer) {
        register(nextType, serializer);
    }

    @Override
    public BitSerializer<?> getSerializer(Object object) {
        return getSerializer(object.getClass());
    }

    @Override
    public BitSerializer<?> getSerializer(Class<?> objectClass) {
        BitSerializer<?> serializer = classToSerializer.get(objectClass);
        if (serializer == null && objectClass.getSuperclass() != null) {
            return getSerializer(objectClass.getSuperclass());
        }
        return serializer;
    }

    @Override
    public BitTypes getBitTypes() {
        return bitTypes;
    }
}
