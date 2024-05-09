package me.stella.core.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface CinnamonFile {

    File getFile();

    void setFile(File file);

    FileConfiguration getBukkitConfig();

    void setBukkitConfig(FileConfiguration config);

    default void load(File file) {
        setFile(file.getAbsoluteFile());
        setBukkitConfig(YamlConfiguration.loadConfiguration(file));
    }

}
