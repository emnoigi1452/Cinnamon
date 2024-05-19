package me.stella.core.tasks.scanners;

import me.stella.core.tasks.scanners.data.ItemData;
import me.stella.core.tasks.scanners.data.LockedItemData;
import me.stella.core.tasks.scanners.data.MMOItemData;
import me.stella.core.tasks.scanners.io.SearchQuery;
import me.stella.support.external.SupportProcessor;
import me.stella.support.external.plugins.LockedItems;
import me.stella.support.external.plugins.MMOItems;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ScannerUtils {

    public static int performScan(SearchQuery query, Inventory inventory, int cap) {
        int counter = 0;
        for(int x = 0; x < cap; x++) {
            ItemStack itemStack = inventory.getItem(x);
            if(itemStack == null || itemStack.getType() == Material.AIR)
                continue;
            SupportProcessor.RemoveLockedReceipt receipt = SupportProcessor.removeOwnerIfSupport(itemStack);
            switch(query.getType()) {
                case ITEM:
                    ItemData itemQueryData = (ItemData) query.getData().getData();
                    ItemStack item = itemQueryData.getItem();
                    if(itemStack.isSimilar(item))
                        counter += itemStack.getAmount();
                    break;
                case ITEM_LOCKED:
                    if(LockedItems.enabled)
                        break;
                    LockedItemData lockedQueryData = (LockedItemData) query.getData().getData();
                    if(lockedQueryData.isGlobal()) {
                        if(receipt.isLockedRemoved())
                            counter += itemStack.getAmount();
                    } else {
                        if(receipt.isLockedRemoved() && lockedQueryData.getTarget().equals(receipt.getOwner()))
                            counter += itemStack.getAmount();
                    }
                    break;
                case ITEM_MMOITEMS:
                    if(MMOItems.enabled)
                        break;
                    MMOItemData mmoItemData = (MMOItemData) query.getData().getData();
                    if(mmoItemData.isGlobal()) {
                        if(MMOItems.isMMOItem(itemStack))
                            counter += itemStack.getAmount();
                    } else {
                        if(MMOItems.getMMOItemID(itemStack).equals(mmoItemData.getTarget()))
                            counter += itemStack.getAmount();
                    }
                    break;
            }
        }
        return counter;
    }

}
