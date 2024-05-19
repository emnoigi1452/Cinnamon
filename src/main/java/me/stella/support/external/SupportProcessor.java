package me.stella.support.external;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.stella.support.external.plugins.LockedItems;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SupportProcessor {

    public static final RemoveLockedReceipt EMPTY = RemoveLockedReceipt.buildReceipt(false, "");

    public static RemoveLockedReceipt removeOwnerIfSupport(@NotNull ItemStack itemStack) {
        if(LockedItems.enabled)
            return SupportProcessor.EMPTY;
        if(LockedItems.isLocked(itemStack)) {
            RemoveLockedReceipt receipt = RemoveLockedReceipt.buildReceipt(true, itemStack.clone());
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace(" " + LockedItems.getOwnerTag(itemStack), ""));
            itemStack.setItemMeta(meta);
            return receipt;
        }
        return SupportProcessor.EMPTY;
    }

    public static class RemoveLockedReceipt {
        private final boolean removed;
        private final String owner;

        private RemoveLockedReceipt(boolean removed, String owner) {
            this.removed = removed;
            this.owner = owner;
        }

        public final boolean isLockedRemoved() {
            return this.removed;
        }

        public final String getOwner() {
            return this.owner;
        }

        public static RemoveLockedReceipt buildReceipt(boolean isRemoved, @Nullable String owner) {
            return (new RemoveLockedReceipt(isRemoved, owner));
        }

        public static RemoveLockedReceipt buildReceipt(boolean isRemoved, @Nullable ItemStack original) {
            return (new RemoveLockedReceipt(isRemoved, LockedItems.getOwnerName(original)));
        }
    }

}
