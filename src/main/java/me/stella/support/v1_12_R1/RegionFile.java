package me.stella.support.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.SupportFrame;

import java.util.HashMap;
import java.util.Map;

public class RegionFile implements SupportFrame {

    private final ClassWrapper<?> wrapper;
    private final Map<String, String> fields;

    public RegionFile() {
        this.wrapper = Reflector.load(getDirectory());
        this.fields = new HashMap<>();
    }

    @Override
    public void init() {
        fields.put("offset", "d");
    }

    @Override
    public Map<String, String> fields() {
        return this.fields;
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.RegionFile";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.wrapper;
    }
}
