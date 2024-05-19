package me.stella.support.minecraft.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class NBTTagList implements SupportFrame {

    private final ClassWrapper<?> classNBTTagList;
    private final Map<String, MethodWrapper> methods;

    public NBTTagList() {
        this.classNBTTagList = Reflector.load(getDirectory());
        this.methods = new HashMap<>();
    }

    @Override
    public void init() {
        ClassWrapper<?> classNBTBase = ClassLibrary.getSupportFor("NBTBase").getClassWrapper();
        Map<String, MethodWrapper> methods = new HashMap<>();
        methods.put("sort", MethodWrapper.of("sort", Comparator.class));
        methods.put("add", MethodWrapper.of("add", classNBTBase.getWrappingClass()));
        methods.put("set", MethodWrapper.of("set", int.class, classNBTBase.getWrappingClass()));
        methods.put("remove", MethodWrapper.of("remove", int.class));
        methods.put("isEmpty", MethodWrapper.of("isEmpty"));
        methods.put("size", MethodWrapper.of("size"));
        methods.put("getTag", MethodWrapper.of("get", int.class));
        methods.put("getInt", MethodWrapper.of("c", int.class));
        methods.put("getIntArray", MethodWrapper.of("d", int.class));
        methods.put("getDouble", MethodWrapper.of("f", int.class));
        methods.put("getFloat", MethodWrapper.of("g", int.class));
        methods.put("getString", MethodWrapper.of("getString", int.class));
        methods.put("get", MethodWrapper.of("i", int.class));
        this.methods.putAll(methods);
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.NBTTagList";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.classNBTTagList;
    }

    @Override
    public Map<String, MethodWrapper> methods() {
        return this.methods;
    }
}
