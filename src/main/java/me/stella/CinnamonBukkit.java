package me.stella;

import me.lucko.helper.Schedulers;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.promise.Promise;
import me.stella.core.storage.CinnamonConfig;
import me.stella.core.storage.CinnamonLocale;
import me.stella.core.storage.branches.config.ChunkMap;
import me.stella.core.storage.branches.config.UUIDMap;
import me.stella.core.tasks.background.AsyncChunkMapper;
import me.stella.core.tasks.background.AsyncUUIDMapper;
import me.stella.support.ClassLibrary;
import me.stella.support.external.plugins.LockedItems;
import me.stella.support.external.plugins.MMOItems;
import org.bukkit.plugin.PluginLoadOrder;

import java.util.logging.Logger;
import java.util.stream.Collectors;


@Plugin(
        name = "Cinnamon",
        version = CinnamonBukkit.version,
        description = "An advanced, reflective system for tracking items.",
        authors = {"StellarSeal_"},
        softDepends = {"PlaceholderAPI"},
        load = PluginLoadOrder.POSTWORLD
)
public final class CinnamonBukkit extends ExtendedJavaPlugin {

    public final static String version = "1.0-alpha01";
    public static final Logger console = Logger.getLogger("Minecraft");
    private static CinnamonBukkit main;
    public static boolean debug = false;

    @Override
    protected void enable() {
        main = this;
        CinnamonUtils.loadNativeClasses();
        saveDefaultConfig();
        saveResource("lang.yml", false);
        CinnamonConfig config = new CinnamonConfig(main);
        CinnamonLocale locale = new CinnamonLocale(main);
        CinnamonTable.inject(CinnamonConfig.class, config);
        CinnamonTable.inject(CinnamonLocale.class, new CinnamonLocale(main));
        debug = config.enableDebugMode();
        CinnamonUtils.logInfo(locale.getMessage("plugin.on")
                .replace("{version}", version));
        Promise.start().thenRunSync(() -> ClassLibrary.init(this.getServer()))
                .thenRunAsync(CinnamonBukkit::loadComponents)
                .thenRunSync(CinnamonBukkit::loadExternalSupport);
        Schedulers.async().runRepeating(() -> {
           try {
               System.gc();
           } catch(Exception err) { err.printStackTrace(); }
        }, 200L, 600L);
    }

    public static void loadExternalSupport() {
        LockedItems.enabled = CinnamonUtils.isPluginEnabled("LockedItems");
        MMOItems.enabled = CinnamonUtils.isPluginEnabled("MMOItems");
    }

    public static void loadComponents() {
        CinnamonConfig config = CinnamonTable.get(CinnamonConfig.class);
        CinnamonLocale locale = CinnamonTable.get(CinnamonLocale.class);
        Schedulers.async().run(() -> {
            try {
                ChunkMap chunkMapConfig = config.getChunkCacheSettings();
                UUIDMap uidMapConfig = config.getUUIDCacheSettings();
                CinnamonTable.inject(ChunkMap.class, chunkMapConfig);
                CinnamonTable.inject(UUIDMap.class, uidMapConfig);
                AsyncUUIDMapper uidMapper = new AsyncUUIDMapper();
                AsyncChunkMapper chunkMapper = new AsyncChunkMapper();
                uidMapper.load(uidMapConfig.isEnabled(), uidMapConfig.getInterval());
                chunkMapper.load(chunkMapConfig.isEnabled(), chunkMapConfig.getInterval(), chunkMapper.excludeEmptyChunks());
                CinnamonTable.inject(AsyncUUIDMapper.class, uidMapper);
                CinnamonTable.inject(AsyncChunkMapper.class, chunkMapper);
                Promise.start().thenApplyAsync(n -> locale.getComponentsInfo().stream().map(
                        str -> str.replace("{mc_ver}", ClassLibrary.version)
                        .replace("{uid_cache_status}", CinnamonUtils.toOnOff(uidMapConfig.isEnabled()))
                        .replace("{chunk_cache_status}", CinnamonUtils.toOnOff(chunkMapConfig.isEnabled()))
                ).collect(Collectors.toList()));
            } catch(Exception err) { err.printStackTrace(); }
        });
    }

    public static ClassLoader getLoader() {
        return main.getClass().getClassLoader();
    }

    public static CinnamonBukkit getMain() {
        return main;
    }

    @Override
    protected void disable() {
        AsyncUUIDMapper uuidMapper = CinnamonTable.get(AsyncUUIDMapper.class);
        if(uuidMapper.isEnabled())
            uuidMapper.getMapperTask().close();
        AsyncChunkMapper chunkMapper = CinnamonTable.get(AsyncChunkMapper.class);
        if(chunkMapper.isEnabled())
            chunkMapper.getMapperTask().close();
    }
}
