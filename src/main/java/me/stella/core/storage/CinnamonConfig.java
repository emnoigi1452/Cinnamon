package me.stella.core.storage;

import me.stella.CinnamonBukkit;
import me.stella.core.storage.branches.config.ChunkMap;
import me.stella.core.storage.branches.config.UUIDMap;
import org.bukkit.configuration.ConfigurationSection;
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

    public ChunkMap getChunkCacheSettings() {
        ConfigurationSection chunkCache = getBukkitConfig().getConfigurationSection("engine.chunk-map");
        return new ChunkMap(chunkCache.getBoolean("enabled", false),
                chunkCache.getLong("interval", 72000L), chunkCache.getBoolean("skip-empty-chunks", true));
    }

    public UUIDMap getUUIDCacheSettings() {
        ConfigurationSection chunkCache = getBukkitConfig().getConfigurationSection("engine.uuid-map");
        return new UUIDMap(chunkCache.getBoolean("enabled", false),
                chunkCache.getLong("interval", 72000L));
    }

    public boolean enableDebugMode() {
        return this.config.getBoolean("engine.debug", false);
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
