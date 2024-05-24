package me.stella.core.tasks.scanners.io;

import me.stella.core.tasks.scanners.ScannerUtils;
import me.stella.core.tasks.scanners.data.impl.EnchantmentData;
import me.stella.core.tasks.scanners.data.impl.ItemData;
import me.stella.core.tasks.scanners.data.impl.LockedItemData;
import me.stella.core.tasks.scanners.data.impl.MMOItemData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SearchQuery {

    private final SearchType type;
    private final Query<?> data;

    public SearchQuery(SearchType type, Query<?> data) {
        this.type = type;
        this.data = data;
    }

    public static SearchQuery buildQuery(Player player, String expression) throws Exception {
        String[] expressionParams = expression.split(",");
        switch(expressionParams[0].toLowerCase()) {
            case "locked":
                LockedItemData lockedData = expressionParams.length == 1 ?
                        new LockedItemData() : new LockedItemData(expressionParams[1]);
                return new SearchQuery(SearchType.ITEM_LOCKED, Query.wrap(lockedData));
            case "mmoitems":
                MMOItemData mmoData = expressionParams.length == 1 ?
                        new MMOItemData() : new MMOItemData(expressionParams[1]);
                return new SearchQuery(SearchType.ITEM_MMOITEMS, Query.wrap(mmoData));
            case "item":
                PlayerInventory inventory = player.getInventory();
                ItemStack parse;
                if(expressionParams.length != 2)
                    throw new RuntimeException("Invalid item position!");
                switch(expressionParams[1].toLowerCase()) {
                    case "hand":
                        parse = inventory.getItemInMainHand();
                        break;
                    case "offhand":
                        parse = inventory.getItemInOffHand();
                        break;
                    default:
                        parse = inventory.getItem(ScannerUtils.parseInt(expressionParams[1]));
                        break;
                }
                assert parse != null && parse.getType() != Material.AIR;
                return new SearchQuery(SearchType.ITEM, Query.wrap(new ItemData(parse)));
            case "enchant":
                if(expressionParams.length != 4)
                    throw new RuntimeException("Invalid enchantment info!");
                Enchantment enchantment = Enchantment.getByName(expressionParams[1]);
                if(enchantment == null)
                    throw new RuntimeException("Invalid enchantment!");
                int level = 0; String operation = "=";
                try {
                    int i = 0; StringBuilder builder = new StringBuilder();
                    String raw = expressionParams[3];
                    for(; i < 2; i++)
                        builder.append(ScannerUtils.validateSymbol(raw.charAt(i)));
                    String parsed = builder.toString().trim();
                    if(!parsed.isEmpty())
                        operation = parsed;
                    level = ScannerUtils.parseInt(raw.substring(operation.length()));
                } catch(Exception err) {
                    throw new RuntimeException("Invalid comparison expression!");
                }
                return new SearchQuery(SearchType.ITEM_ENCHANT,
                        Query.wrap(new EnchantmentData(enchantment, level, ScannerUtils.getOperation(operation))));
        }
        return null;
    }

    public final SearchType getType() {
        return this.type;
    }

    public final Query<?> getData() {
        return this.data;
    }

    public enum SearchType {
        ITEM, ITEM_LOCKED, ITEM_MMOITEMS, ITEM_ENCHANT
    }

    public static class Query<T> {

        private final T data;

        protected Query(T data) {
            this.data = data;
        }

        public static <T> Query<T> wrap (T data) {
            return (new Query<>(data));
        }

        public T getData() {
            return this.data;
        }

    }

}
