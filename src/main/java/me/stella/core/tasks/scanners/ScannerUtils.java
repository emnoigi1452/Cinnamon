package me.stella.core.tasks.scanners;

import me.stella.core.tasks.scanners.data.impl.EnchantmentData;
import me.stella.core.tasks.scanners.data.impl.ItemData;
import me.stella.core.tasks.scanners.data.impl.LockedItemData;
import me.stella.core.tasks.scanners.data.impl.MMOItemData;
import me.stella.core.tasks.scanners.io.SearchQuery;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.external.SupportProcessor;
import me.stella.support.external.plugins.LockedItems;
import me.stella.support.external.plugins.MMOItems;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ScannerUtils {

    public static final List<Character> comparativeSymbols = Arrays.asList('>', '<', '=');

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
                case ITEM_ENCHANT:
                    EnchantmentData data = (EnchantmentData) query.getData().getData();
                    int level = itemStack.getEnchantmentLevel(data.getEnchantment());
                    if(data.compare(level, data.getLevel()))
                        counter += itemStack.getAmount();
                    break;
            }
        }
        return counter;
    }

    public static String getOperation(String operationExpression) {
        switch(operationExpression) {
            case ">":
                return "greater";
            case "<":
                return "smaller";
            case "=":
                return "equal";
            case ">=":
                return "greaterOrEqual";
            case "<=":
                return "smallerOrEqual";
            default:
                throw new RuntimeException("Invalid expression!");
        }
    }

    public static int parseInt(String param) {
        try {
            return Integer.parseInt(param);
        } catch(Exception err) { return 0; }
    }

    public static boolean isNumberTag(ObjectWrapper<?> tag) {
        return tag.getObjectType().getGenericSuperclass().getTypeName().contains("NBTNumber");
    }

    public static byte getByteSize(Number num) {
       String numType = num.getClass().getSimpleName();
       switch(numType) {
           case "Byte":
               return 1;
           case "Short":
               return 2;
           case "Integer":
           case "Float":
               return 4;
           case "Long":
           case "Double":
               return 8;

       }
       return -1;
    }

    public static char validateSymbol(char c) {
        if(comparativeSymbols.contains(c))
            return c;
        return ' ';
    }
}
