package me.stella.reflection;

import com.sun.istack.internal.NotNull;

public class ObjectCaster {

    protected static boolean validate(@NotNull Object object, Class<?>... types) {
        for(Class<?> type: types) {
            if(object.getClass().equals(type))
                return true;
        }
        return false;
    }

    public static Integer toInteger(@NotNull Object object) {
        assert validate(object, int.class, Integer.class);
        return Integer.parseInt(String.valueOf(object).trim());
    }

    public static Byte toByte(@NotNull Object object) {
        assert validate(object, byte.class, Byte.class);
        return Byte.parseByte(String.valueOf(object).trim());
    }

    public static Boolean toBoolean(@NotNull Object object) {
        assert validate(object, boolean.class, Boolean.class);
        return Boolean.getBoolean(String.valueOf(object).trim());
    }

    public static int[] toIntArray(@NotNull Object object) {
        assert object.getClass().getName().startsWith("[I");
        return (int[]) object;
    }

}
