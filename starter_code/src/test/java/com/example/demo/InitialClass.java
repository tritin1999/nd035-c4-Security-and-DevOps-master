package com.example.demo;

import java.lang.reflect.Field;

public class InitialClass {
    public static void setUp(Object target, String property, Object source) {
        boolean accessible = false;
        try {
            Field f = target.getClass().getDeclaredField(property);
            if (!f.isAccessible()) {
                f.setAccessible(true);
                accessible = true;
            }
            f.set(target, source);
            if (accessible) {
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
