package me.stella.core.storage;

import me.stella.CinnamonBukkit;
import me.stella.CinnamonUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class CinnamonLocale implements CinnamonFile {

    private File file;
    private FileConfiguration config;

    public CinnamonLocale(CinnamonBukkit main) {
        File file = new File(main.getDataFolder(), "config.yml");
        if(!file.exists())
            throw new RuntimeException("Configuration file is not generated! Please try reloading...");
        load(file);
    }

    public String getMessage(String key) {
        return getMessage(key, true);
    }

    public String getMessage(String key, boolean prefix) {
        StringBuilder builder = new StringBuilder();
        if(prefix)
            builder.append(this.config.getString("prefix"));
        builder.append(this.config.getString("messages." + key));
        return CinnamonUtils.color(builder.toString());
    }

    public List<String> getComponentsInfo() {
        return this.config.getStringList("components")
                .stream().map(CinnamonUtils::color)
                .collect(Collectors.toList());
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

    public String getDebugMessage(String key) {
        return CinnamonUtils.color(this.config.getString("debug." + key));
    }
}
