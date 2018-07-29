package org.jusecase.bitpack;

import java.util.HashMap;
import java.util.Map;

public class BitTypes {
    private final Map<Integer, Class<?>> classForType = new HashMap<>();
    private final Map<Class<?>, Integer> typeForClass = new HashMap<>();

    public void register(int type, Class<?> clazz) {
        classForType.put(type, clazz);
        typeForClass.put(clazz, type);
    }

    public Class<?> getClassForType(int type) {
        return classForType.get(type);
    }


    public int getTypeForClass(Class<?> clazz) {
        Integer type = typeForClass.get(clazz);
        if (type == null) {
            throw new NullPointerException("No bit type found for class " + clazz);
        }
        return type;
    }

    public int getTypeForInstance(Object instance) {
        return getTypeForClass(instance.getClass());
    }

    public int getCount() {
        return typeForClass.size();
    }
}
