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
import me.stella.objects.AutoCloseJavaScheduler;
import me.stella.objects.CinnamonTable;
import me.stella.support.ClassLibrary;
import me.stella.support.external.plugins.LockedItems;
import me.stella.support.external.plugins.MMOItems;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Plugin(
        name = "Cinnamon",
        version = CinnamonBukkit.version,
        description = "An advanced, reflective system for tracking items.",
        authors = {"StellarSeal_"},
        softDepends = {"PlaceholderAPI"},
        apiVersion = "1.13"
)
public final class CinnamonBukkit extends ExtendedJavaPlugin {

    public final static String version = "0.1-alpha04";
    public static final Logger console = Logger.getLogger("Minecraft");
    private static final AutoCloseJavaScheduler javaScheduler = new AutoCloseJavaScheduler(
            Executors.newScheduledThreadPool(256));
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
        CinnamonTable.inject(CinnamonLocale.class, locale);
        debug = config.enableDebugMode();
        CinnamonUtils.logInfo(locale.getMessage("plugin-on")
                .replace("{version}", version));
        Promise.start().thenRunSync(() -> ClassLibrary.init(this.getServer()))
                .thenRunAsync(CinnamonBukkit::loadComponents)
                .thenRunSync(CinnamonBukkit::loadExternalSupport);
        javaScheduler.scheduleAtFixedRate(() -> {
            try {
                System.gc();
            } catch(Exception err) { err.printStackTrace(); }
        }, 10L, 30L, TimeUnit.SECONDS);
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
                        str -> str.replace("{mcver}", ClassLibrary.version)
                                .replace("{uid_cache_status}", CinnamonUtils.toOnOff(uidMapConfig.isEnabled()))
                                .replace("{chunk_cache_status}", CinnamonUtils.toOnOff(chunkMapConfig.isEnabled()))
                ).collect(Collectors.toList())).thenApplySync(msg -> {
                    msg.forEach(CinnamonUtils::logInfo);
                    return null;
                });
            } catch(Exception err) { err.printStackTrace(); }
        });
    }

    @Override
    protected void disable() {
        final AutoCloseJavaScheduler scheduler = getJavaScheduler();
        (new Thread(() -> {
            try {
                scheduler.shutdown();
            } catch(Exception err) { err.printStackTrace(); }
        })).start();
        CinnamonTable.get(AsyncUUIDMapper.class).closeIfEnabled();
        CinnamonTable.get(AsyncChunkMapper.class).closeIfEnabled();
    }

    public static ClassLoader getLoader() {
        return main.getClass().getClassLoader();
    }

    public static CinnamonBukkit getMain() {
        return main;
    }

    public static AutoCloseJavaScheduler getJavaScheduler() {
        return javaScheduler;
    }
}
