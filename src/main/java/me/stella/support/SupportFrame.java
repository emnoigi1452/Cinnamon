package me.stella.support;

import me.stella.reflection.ClassWrapper;
import me.stella.reflection.MethodWrapper;
import me.stella.reflection.ObjectWrapper;

import java.util.Collections;
import java.util.Map;

public interface SupportFrame {

    default String getVersion() {
        Package supportPackage = getClass().getPackage();
        return supportPackage.getName().split("\\.")[3];
    }
    void init();
    String getDirectory();
    ClassWrapper<?> getClassWrapper();
    default Map<String, String> staticFields() {
        return Collections.emptyMap();
    }
    default Map<String, String> fields() {
        return Collections.emptyMap();
    }
    default Map<String, MethodWrapper> staticMethods() {
        return Collections.emptyMap();
    }
    default Map<String, MethodWrapper> methods() {
        return Collections.emptyMap();
    }
    default ObjectWrapper<?> invokeMethod(String key, ObjectWrapper<?> objectWrapper, Object... parameters) {
        return invokeMethod(key, objectWrapper.getObject(), parameters);
    }
    default ObjectWrapper<?> invokeMethod(String key, Object object, Object... parameters) {
        Map<String, MethodWrapper> methods = methods();
        if(!methods.containsKey(key))
            return null;
        MethodWrapper method = methods.get(key);
        return (new ObjectWrapper<>(object))
                .invokeMethod(method.getMethodName(), method.getParameters().getParameters(), parameters);
    }
    default ObjectWrapper<?> invokeStaticMethod(String key, Object... parameters) {
        Map<String, MethodWrapper> staticMethods = staticMethods();
        if(!staticMethods.containsKey(key))
            return null;
        MethodWrapper method = staticMethods.get(key);
        return getClassWrapper()
                .invokeMethod(method.getMethodName(), method.getParameters().getParameters(), parameters);
    }
    default ObjectWrapper<?> accessField(String key, ObjectWrapper<?> objectWrapper) {
        return accessField(key, objectWrapper.getObject());
    }
    default ObjectWrapper<?> accessField(String key, Object object) {
        ObjectWrapper<?> wrapper = new ObjectWrapper<>(object);
        Map<String, String> fields = fields();
        if(!fields.containsKey(key))
            return null;
        return wrapper.getField(fields.get(key));
    }
    default ObjectWrapper<?> accessStaticField(String key) {
        Map<String, String> fields = staticFields();
        if(!fields.containsKey(key))
            return null;
        return new ObjectWrapper<>(getClassWrapper().getStaticField(fields.get(key)));
    }

}
