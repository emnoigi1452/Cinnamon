package me.stella.support.minecraft.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.SupportFrame;

import java.util.Collections;
import java.util.Map;

public class NBTBase implements SupportFrame {

    private final ClassWrapper<?> classNBTBase;

    public NBTBase() {
        this.classNBTBase = Reflector.load(getDirectory());
    }

    @Override
    public void init() {

    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.NBTBase";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.classNBTBase;
    }

    @Override
    public Map<String, MethodWrapper> methods() {
        return Collections.singletonMap("clone", MethodWrapper.of("clone"));
    }
}
