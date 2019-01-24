package org.jusecase.bitpack;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AbstractBitProtocol implements BitProtocol {
    private final Map<Class<?>, BitSerializer<?>> classToSerializer = new HashMap<>();
    private final BitTypes bitTypes = new BitTypes();

    private int nextType = 1;

    @Override
    public <T> void register(int objectType, BitSerializer<T> serializer) {
        Class<T> objectClass = resolveObjectClass(serializer);
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

    @SuppressWarnings("unchecked")
    private <T> Class<T> resolveObjectClass(BitSerializer<T> serializer) {
        for (Type type : serializer.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == BitSerializer.class) {
                    return (Class<T>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }

        throw new UnsupportedOperationException("Failed to determine object class of serializer " + serializer);
    }
}
