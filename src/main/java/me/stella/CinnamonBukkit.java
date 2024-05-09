package me.stella;

import me.stella.support.ClassLibrary;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class CinnamonBukkit extends JavaPlugin {

    public static final Logger console = Logger.getLogger("Minecraft");

    private static CinnamonBukkit main;

    @Override
    public void onEnable() {
        main = this;
        ClassLibrary.init(this.getServer());
    }

    public static ClassLoader getLoader() {
        return main.getClass().getClassLoader();
    }

    public static CinnamonBukkit getMain() {
        return main;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
