package me.stella.support.minecraft.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.SupportFrame;

import java.util.HashMap;
import java.util.Map;

public class NBTNumber implements SupportFrame {

    private final ClassWrapper<?> wrapper;
    private final Map<String, MethodWrapper> methods;

    public NBTNumber() {
        this.wrapper = Reflector.load(getDirectory());
        this.methods = new HashMap<>();
    }

    @Override
    public Map<String, MethodWrapper> methods() {
        return this.methods;
    }

    @Override
    public void init() {
        this.methods.put("asNumber", MethodWrapper.of("i"));
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.NBTNumber";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.wrapper;
    }
}
