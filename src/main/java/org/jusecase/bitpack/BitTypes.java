package org.jusecase.bitpack;

import java.util.HashMap;
import java.util.Map;

public class BitTypes {
    private final Map<Integer, Class<?>> classForType = new HashMap<>();
    private final Map<Class<?>, Integer> typeForClass = new HashMap<>();

    private int requiredBits = 1;

    public void register(int type, Class<?> clazz) {
        classForType.put(type, clazz);
        typeForClass.put(clazz, type);

        requiredBits = -1;
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

    public int getRequiredBits() {
        if (requiredBits < 0) {
            int count = getCount();
            if (count < 1) {
                requiredBits = 1;
            } else {
                requiredBits = (int) Math.ceil(Math.sqrt(count));
            }
        }

        return requiredBits;
    }
}
