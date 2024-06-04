package me.stella.support;

import me.stella.CinnamonTable;
import me.stella.CinnamonUtils;
import me.stella.core.decompress.world.WorldAlgorithm;
import me.stella.core.decompress.world.WorldDeserializer;
import me.stella.core.storage.CinnamonLocale;
import org.bukkit.Server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClassLibrary {

    public static final Map<String, SupportFrame> frames = new HashMap<>();
    public static String version;

    @SuppressWarnings("unchecked")
    public static void init(Server server) {
        CinnamonLocale locale = CinnamonTable.get(CinnamonLocale.class);
        String path = "me.stella.support.minecraft.{version}.";
        try {
            ClassLibrary.version = server.getClass().getName().split("\\.")[3];
            ClassDictionary versionDictionary = ClassDictionary.valueOf(version);
            String classLoading = locale.getDebugMessage("loaded-class");
            CinnamonUtils.debug("Loading classes: " + Arrays.toString(versionDictionary.getClasses()));
            for(String className: versionDictionary.getClasses()) {
                Class<SupportFrame> classSupport = (Class<SupportFrame>) Class.forName(
                        path.replace("{version}", version) + className);
                SupportFrame frame = (SupportFrame) classSupport.getConstructors()[0].newInstance();
                CinnamonUtils.debug(classLoading.replace("{class}", frame.getDirectory()));
                inject(className, frame);
            }
            frames.values().forEach(SupportFrame::init);
            for(WorldAlgorithm algorithm: WorldAlgorithm.algorithms()) {
                if(isBefore(algorithm.getLastOperationalVersion(), ClassLibrary.version)) {
                    WorldDeserializer.setAlgorithm(algorithm);
                    break;
                }
            }
            if(!(WorldDeserializer.isAlgorithmPresent()))
                throw new RuntimeException("Unsupported game version detected! Please contact the developer!");
            CinnamonUtils.debug(locale.getDebugMessage("world-algo-info")
                    .replace("{algorithm}", WorldDeserializer.getAlgorithmID()));
        } catch(Exception err) { err.printStackTrace(); }
    }

    public static boolean isBefore(String version, String param) {
        String[] a = version.split("_"), b = param.split("_");
        int v1 = Integer.parseInt(a[1]), v2 = Integer.parseInt(b[1]);
        int r1 = Integer.parseInt(a[2].substring(1)), r2 = Integer.parseInt(b[2].substring(1));
        return v1 >= v2 && r1 >= r2;
    }

    public static void inject(String name, SupportFrame implementation) {
        frames.put(name, implementation);
    }

    public static SupportFrame getSupportFor(String name) {
        return frames.getOrDefault(name, null);
    }

}
