package me.stella.core.tasks.scanners.data;

import org.bukkit.inventory.ItemStack;

public class ItemData {

    private final ItemStack item;

    public ItemData(ItemStack item) {
        this.item = item;
    }

    public final ItemStack getItem() {
        return this.item;
    }

}
