package me.stella.support.minecraft.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class NBTCompressedStreamTools implements SupportFrame {

    private final Map<String, MethodWrapper> staticMethods;
    private final ClassWrapper<?> classNBTCompressedStreamTools;

    public NBTCompressedStreamTools() {
        this.classNBTCompressedStreamTools = Reflector.load(getDirectory());
        this.staticMethods = new HashMap<>();
    }

    @Override
    public void init() {
        ClassWrapper<?> classNBTTagCompound = ClassLibrary.getSupportFor("NBTTagCompound").getClassWrapper();
        Map<String, MethodWrapper> staticMethods = new HashMap<>();
        staticMethods.put("readNBT", MethodWrapper.of("a", InputStream.class));
        staticMethods.put("writeNBT", MethodWrapper.of("a", classNBTTagCompound.getWrappingClass(), OutputStream.class));
        this.staticMethods.putAll(staticMethods);
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.NBTCompressedStreamTools";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.classNBTCompressedStreamTools;
    }


    @Override
    public Map<String, MethodWrapper> staticMethods() {
        return this.staticMethods;
    }
}
