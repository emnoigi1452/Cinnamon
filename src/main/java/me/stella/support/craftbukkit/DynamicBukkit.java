package me.stella.support.craftbukkit;

import me.stella.CinnamonBukkit;
import me.stella.CinnamonUtils;
import me.stella.reflection.ClassWrapper;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.ClassLibrary;
import org.bukkit.inventory.ItemStack;

public class DynamicBukkit {

    private static ClassWrapper<?> getCraftItemStack() {
        String path = "org.bukkit.craftbukkit." + ClassLibrary.version + ".inventory.CraftItemStack";
        try {
            Class<?> craftItemStack = Class.forName(path, true, CinnamonUtils.getServerClassLoader());
            return new ClassWrapper<>(craftItemStack);
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

    public static ObjectWrapper<?> toNetStack(ItemStack stack) {
        ClassWrapper<?> craftItemStack = getCraftItemStack();
        try {
            return craftItemStack.invokeMethod("asNMSCopy", new Class<?>[]{ ItemStack.class },
                    new Object[]{stack});
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

    public static ItemStack toBukkitStack(ObjectWrapper<?> netStack) {
        ClassWrapper<?> craftItemStack = getCraftItemStack();
        ClassWrapper<?> itemStack = ClassLibrary.getSupportFor("ItemStack").getClassWrapper();
        try {
            return (ItemStack) craftItemStack.invokeMethod("asBukkitCopy", new Class<?>[]{itemStack.getWrappingClass()},
                    new Object[]{netStack.getObject()}).getObject();
        } catch(Exception err) { err.printStackTrace(); }
        return null;
    }

}
