package me.stella.reflection;

import org.bukkit.Bukkit;

public class Reflector {

    private static final ClassLoader loader;

    static {
        loader = Bukkit.getServer().getClass().getClassLoader();
    }

    public static ClassWrapper<?> load(String path) {
        try {
            Class<?> clazz = Class.forName(path, true, loader);
            return new ClassWrapper<>(clazz);
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }


}
