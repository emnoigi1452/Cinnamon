package me.stella.core.storage;

import me.stella.CinnamonBukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class CinnamonConfig implements CinnamonFile {

    private File file;
    private FileConfiguration config;

    public CinnamonConfig(CinnamonBukkit main) {
        File file = new File(main.getDataFolder(), "config.yml");
        if(!file.exists())
            throw new RuntimeException("Configuration file is not generated! Please try reloading...");
        load(file);
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public FileConfiguration getBukkitConfig() {
        return this.config;
    }

    @Override
    public void setBukkitConfig(FileConfiguration config) {
        this.config = config;
    }
}
