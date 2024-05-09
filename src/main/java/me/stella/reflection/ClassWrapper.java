package me.stella.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassWrapper<T> {

    private final Class<T> classObject;

    public ClassWrapper(Class<T> clazz) {
        this.classObject = clazz;
    }

    public ObjectWrapper<T> newInstance(Class<?>[] types, Object... parameters) {
        try {
            Constructor<T> constructor = classObject.getConstructor(types);
            constructor.setAccessible(true);
            return new ObjectWrapper<>(constructor.newInstance(parameters));
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

    public ObjectWrapper<T> cast(ObjectWrapper<?> objectWrapper) {
        return cast(objectWrapper.getObject());
    }

    @SuppressWarnings("unchecked")
    public ObjectWrapper<T> cast(Object object) {
        assert classObject.isInstance(object);
        return (ObjectWrapper<T>) new ObjectWrapper<>(object);
    }

    @SuppressWarnings("unchecked")
    public <V> ObjectWrapper<V> getStaticField(String field) {
        try {
            Field fieldObject = classObject.getDeclaredField(field);
            fieldObject.setAccessible(true);
            return (ObjectWrapper<V>) new ObjectWrapper<>(fieldObject.get(null));
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <V> ObjectWrapper<V> invokeMethod(String method, Class<?>[] types, Object[] parameters) {
        try {
            Method toInvoke = classObject.getDeclaredMethod(method, types);
            toInvoke.setAccessible(true);
            return (ObjectWrapper<V>) new ObjectWrapper<>(toInvoke.invoke(null, parameters));
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

    public Class<?> getWrappingClass() {
        return this.classObject;
    }
}
