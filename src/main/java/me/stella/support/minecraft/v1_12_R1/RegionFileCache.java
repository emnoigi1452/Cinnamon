package me.stella.support.minecraft.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.SupportFrame;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RegionFileCache implements SupportFrame {

    private Map<String, MethodWrapper> staticMethods;
    private final ClassWrapper<?> wrapper;

    public RegionFileCache() {
        this.wrapper = Reflector.load(getDirectory());
    }

    @Override
    public void init() {
        Map<String, MethodWrapper> sm = new HashMap<>();
        sm.put("loadChunkData", MethodWrapper.of("d", File.class, int.class, int.class));
        sm.put("chunkExists", MethodWrapper.of("chunkExists", File.class, int.class, int.class));
        this.staticMethods = sm;
    }

    @Override
    public Map<String, MethodWrapper> staticMethods() {
        return this.staticMethods;
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.RegionFileCache";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.wrapper;
    }
}
