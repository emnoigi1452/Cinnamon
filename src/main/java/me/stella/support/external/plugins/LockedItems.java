package me.stella.support.external.plugins;


import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockedItems {

    public static boolean enabled;

    public static boolean isLocked(@NotNull ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if(!meta.hasDisplayName())
            return false;
        String name = meta.getDisplayName();
        return name.matches("(.*) §c\\\\[[a-zA-Z0-9_]+\\\\]");
    }

    public static String getOwnerTag(@NotNull ItemStack stack) {
        assert isLocked(stack);
        String name = stack.getItemMeta().getDisplayName();
        Pattern pattern = Pattern.compile("(.*) (§c\\\\[[a-zA-Z0-9_]+\\\\])");
        Matcher itemMatch = pattern.matcher(name);
        return itemMatch.find() ? itemMatch.group(2) : "";
    }

    public static String getOwnerName(@Nullable ItemStack itemStack) {
        if(itemStack == null)
            return "";
        if(!isLocked(itemStack))
            return "";
        String ownerTag = ChatColor.stripColor(getOwnerTag(itemStack));
        return ownerTag.substring(1, ownerTag.length() - 1);
    }


}
