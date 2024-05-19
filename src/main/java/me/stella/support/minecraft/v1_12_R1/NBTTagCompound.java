package me.stella.support.minecraft.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;

import java.util.HashMap;
import java.util.Map;

public class NBTTagCompound implements SupportFrame {

    private final ClassWrapper<?> classNbtTagCompound;
    private final Map<String, MethodWrapper> methods;

    public NBTTagCompound() {
        this.classNbtTagCompound = Reflector.load(getDirectory());
        methods = new HashMap<>();
    }

    @Override
    public void init() {
        ClassWrapper<?> nbtBaseClass = ClassLibrary.getSupportFor("NBTBase").getClassWrapper();
        Map<String, MethodWrapper> methods = new HashMap<>();
        methods.put("keys", MethodWrapper.of("c"));
        methods.put("size", MethodWrapper.of("d"));
        methods.put("set", MethodWrapper.of("set", String.class, nbtBaseClass.getWrappingClass()));
        methods.put("setByte", MethodWrapper.of("setByte", String.class, byte.class));
        methods.put("setShort", MethodWrapper.of("setShort", String.class, short.class));
        methods.put("setInt", MethodWrapper.of("setInt", String.class, int.class));
        methods.put("setLong", MethodWrapper.of("setLong", String.class, long.class));
        methods.put("setFloat", MethodWrapper.of("setFloat", String.class, float.class));
        methods.put("setDouble", MethodWrapper.of("setDouble", String.class, double.class));
        methods.put("setBoolean", MethodWrapper.of("setBoolean", String.class, boolean.class));
        methods.put("setByteArray", MethodWrapper.of("setByteArray", String.class, byte[].class));
        methods.put("setIntArray", MethodWrapper.of("setIntArray", String.class, int[].class));
        methods.put("setString", MethodWrapper.of("setString", String.class, String.class));
        methods.put("hasKey", MethodWrapper.of("hasKey", String.class));
        methods.put("get", MethodWrapper.of("get", String.class));
        methods.put("getByte", MethodWrapper.of("getByte", String.class));
        methods.put("getShort", MethodWrapper.of("getShort", String.class));
        methods.put("getInt", MethodWrapper.of("getInt", String.class));
        methods.put("getLong", MethodWrapper.of("getLong", String.class));
        methods.put("getFloat", MethodWrapper.of("getFloat", String.class));
        methods.put("getDouble", MethodWrapper.of("getDouble", String.class));
        methods.put("getBoolean", MethodWrapper.of("getBoolean", String.class));
        methods.put("getByteArray", MethodWrapper.of("getByteArray", String.class));
        methods.put("getIntArray", MethodWrapper.of("getIntArray", String.class));
        methods.put("getString", MethodWrapper.of("getString", String.class));
        methods.put("getCompound", MethodWrapper.of("getCompound", String.class));
        methods.put("getList", MethodWrapper.of("getList", String.class, int.class));
        methods.put("remove", MethodWrapper.of("remove", String.class));
        methods.put("isEmpty", MethodWrapper.of("isEmpty"));
        this.methods.putAll(methods);
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.NBTTagCompound";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.classNbtTagCompound;
    }

    @Override
    public Map<String, MethodWrapper> methods() {
        return this.methods;
    }

}
