package me.stella;

import me.stella.reflection.ClassWrapper;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class CinnamonUtils {

    public static final int[] tileEntitySize = new int[] { 9, 18, 27, 36, 45, 54 };

    public static int getMaxInSet(Collection<? extends Integer> intSet) {
        int max = Integer.MIN_VALUE;
        for(Integer i: intSet)
            max = Math.max(max, i);
        return max;
    }

    public static String toOnOff(boolean b) {
        return b ? "&a&l✔" : "&c&l✘";
    }

    public static int getTileEntitySize(int maxSlot) {
        for(Integer size: tileEntitySize) {
            if(maxSlot < size)
                return size;
        }
        return 54;
    }

    public static Server getServer() {
        return CinnamonBukkit.getMain().getServer();
    }

    public static void loadNativeClasses() {
        try {
            Class<?> nativeChatColor = Class.forName("org.bukkit.ChatColor",
                    true, getServer().getClass().getClassLoader());
            //
            Set<ClassWrapper<?>> nativeWrappers = new HashSet<>();
            nativeWrappers.add(new ClassWrapper<>(nativeChatColor));
            //
            CinnamonTable.inject(ClassWrapper.class, nativeWrappers);
        } catch (Exception err) { err.printStackTrace(); }
    }

    public static String color(String param) {
        try {
            ClassWrapper<?> chatColor = CinnamonTable.get(ClassWrapper.class, (wrapper) ->
                    wrapper.getWrappingClass().getName().contains("ChatColor")).stream().findFirst().orElse(null);
            if(chatColor == null)
                throw new RuntimeException("Unable to load native class [ChatColor]");
            return String.valueOf(chatColor.invokeMethod("translateAlternateColorCodes",
                    new Class<?>[] { char.class, String.class }, new Object[]{ '&', param }));
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

    public static boolean isPluginEnabled(String name) {
        PluginManager manager = getServer().getPluginManager();
        return manager.getPlugin(name) != null && manager.isPluginEnabled(name);
    }

    public static void debug(String info) {
        if(CinnamonBukkit.debug)
            logInfo(info);
    }

    public static void logInfo(String param) {
        CinnamonBukkit.console.log(Level.INFO, param);
    }

    public static void logWarning(String param) {
        CinnamonBukkit.console.log(Level.WARNING, param);
    }

    public static void logError(String param) {
        CinnamonBukkit.console.log(Level.SEVERE, param);
    }

}
