package me.stella.support.external.plugins;

import com.sun.istack.internal.NotNull;
import me.stella.reflection.ObjectCaster;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;
import me.stella.support.craftbukkit.DynamicBukkit;
import org.bukkit.inventory.ItemStack;

public class MMOItems {

    public static boolean enabled;

    public static boolean isMMOItem(@NotNull ItemStack item) {
        SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
        SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
        ObjectWrapper<?> nms = DynamicBukkit.toNetStack(item);
        if(nms == null)
            return false;
        if(!ObjectCaster.toBoolean(itemStack.invokeMethod("hasTag", nms)))
            return false;
        ObjectWrapper<?> tagObject = itemStack.invokeMethod("getTag", nms);
        return ObjectCaster.toBoolean(nbtTagCompound.invokeMethod("hasKey",
                tagObject, "MMOITEMS_ITEM_ID"));
    }

    public static String getMMOItemID(@NotNull ItemStack item) {
        SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
        SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
        ObjectWrapper<?> nms = DynamicBukkit.toNetStack(item);
        assert (nms != null) && (isMMOItem(item));
        ObjectWrapper<?> tagObject = itemStack.invokeMethod("getTag", nms);
        return String.valueOf(nbtTagCompound.invokeMethod("getString",
                tagObject, "MMOITEMS_ITEM_ID").getObject());
    }

}
