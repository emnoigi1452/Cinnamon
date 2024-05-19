package me.stella.support.minecraft.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;

import java.util.HashMap;
import java.util.Map;

public class ItemStack implements SupportFrame {

    private final ClassWrapper<?> classWrapper;
    private Map<String, MethodWrapper> methods;

    public ItemStack() {
        this.classWrapper = Reflector.load(getDirectory());
    }

    @Override
    public void init() {
        Map<String, MethodWrapper> m = new HashMap<>();
        m.put("isEmpty", MethodWrapper.of("isEmpty"));
        m.put("save", MethodWrapper.of("save",
                ClassLibrary.getSupportFor("NBTTagCompound").getClassWrapper().getWrappingClass()));
        m.put("getTag", MethodWrapper.of("getTag"));
        m.put("hasTag", MethodWrapper.of("hasTag"));
        this.methods = m;
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.ItemStack";
    }

    @Override
    public Map<String, MethodWrapper> methods() {
        return methods;
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.classWrapper;
    }
}
