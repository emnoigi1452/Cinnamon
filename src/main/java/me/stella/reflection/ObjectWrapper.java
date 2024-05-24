package me.stella.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectWrapper<T> {

    private final Class<T> typeObject;
    private final T object;

    @SuppressWarnings("unchecked")
    public ObjectWrapper(T object) {
        this.object = object;
        this.typeObject = (Class<T>) object.getClass();
    }

    public T getObject() {
        return object;
    }

    public Class<T> getObjectType() {
        return this.typeObject;
    }

    public String getClassName() {
        return typeObject.getSimpleName();
    }


    @SuppressWarnings("unchecked")
    public <V> ObjectWrapper<V> getField(String field) {
        try {
            Field fieldObject = typeObject.getDeclaredField(field);
            fieldObject.setAccessible(true);
            return (ObjectWrapper<V>) new ObjectWrapper<>(fieldObject.get(object));
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <V> ObjectWrapper<V> invokeMethod(String method, Class<?>[] types, Object[] parameters) {
        try {
            Method toInvoke = typeObject.getDeclaredMethod(method, types);
            toInvoke.setAccessible(true);
            return (ObjectWrapper<V>) new ObjectWrapper<>(toInvoke.invoke(object, parameters));
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

}
