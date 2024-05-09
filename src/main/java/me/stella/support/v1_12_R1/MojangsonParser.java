package me.stella.support.v1_12_R1;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.Reflector;
import me.stella.support.SupportFrame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MojangsonParser implements SupportFrame {

    private final ClassWrapper<?> classMojangsonParser;
    private Map<String, MethodWrapper> methods;

    public MojangsonParser() {
        this.classMojangsonParser = Reflector.load(getDirectory());
        methods = new HashMap<>();
    }

    @Override
    public void init() {
        Map<String, MethodWrapper> methods = new HashMap<>();
        methods.put("parse", MethodWrapper.of("a"));
        methods.put("parseList", MethodWrapper.of("j"));
        methods.put("parseTag", MethodWrapper.of("f"));
        this.methods.putAll(methods);
    }

    @Override
    public String getDirectory() {
        return "net.minecraft.server.v1_12_R1.MojangsonParser";
    }

    @Override
    public ClassWrapper<?> getClassWrapper() {
        return this.classMojangsonParser;
    }

    @Override
    public Map<String, String> fields() {
        return Collections.singletonMap("string", "h");
    }

    @Override
    public Map<String, MethodWrapper> staticMethods() {
        return Collections.singletonMap("parse", MethodWrapper.of("parse", String.class));
    }
}
