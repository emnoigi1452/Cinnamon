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

    public static int toInteger(@NotNull Object object) {
        assert validate(object, int.class, Integer.class);
        return Integer.parseInt(String.valueOf(object).trim());
    }


    public static byte toByte(@NotNull Object object) {
        assert validate(object, byte.class, Byte.class);
        return Byte.parseByte(String.valueOf(object).trim());
    }

    public static short toShort(@NotNull Object object) {
        assert validate(object, short.class, Short.class);
        return Short.parseShort(String.valueOf(object).trim());
    }

    public static double toDouble(@NotNull Object object) {
        assert validate(object, double.class, Double.class);
        return Double.parseDouble(String.valueOf(object).trim());
    }

    public static float toFloat(@NotNull Object object) {
        assert validate(object, float.class, Float.class);
        return Float.parseFloat(String.valueOf(object).trim());
    }

    public static boolean toBoolean(@NotNull Object object) {
        assert validate(object, boolean.class, Boolean.class);
        return Boolean.getBoolean(String.valueOf(object).trim());
    }

    public static int[] toIntArray(@NotNull Object object) {
        assert object.getClass().getName().startsWith("[I");
        return (int[]) object;
    }

}
