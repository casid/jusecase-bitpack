package org.jusecase.bitpack;

import java.util.HashMap;
import java.util.Map;

public class BitTypes {
    private final Map<Integer, Class<?>> classForType = new HashMap<>();
    private final Map<Class<?>, Integer> typeForClass = new HashMap<>();

    private int maxType;
    private int requiredBits = 1;

    public void register(int type, Class<?> clazz) {
        classForType.put(type, clazz);
        typeForClass.put(clazz, type);

        requiredBits = -1;
        if (type > maxType) {
            maxType = type;
        }
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

    public int getRequiredBits() {
        if (requiredBits < 0) {
            requiredBits = calculateRequiredBits();
        }
        return requiredBits;
    }

    public int calculateRequiredBits() {
        int possibilities = maxType + 1; // we have the value zero, too
        int values = 2;

        for (int bits = 1; bits < 16; ++bits, values *= 2) {
            if (values >= possibilities) {
                return bits;
            }
        }

        throw new IllegalStateException("Too many possibilities for bit types to represent!");
    }

}
