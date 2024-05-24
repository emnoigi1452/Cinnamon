package me.stella.core.tasks.scanners.data.impl;

import me.stella.core.tasks.scanners.data.ComparativeData;
import me.stella.reflection.ObjectWrapper;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentData implements ComparativeData {

    private final ObjectWrapper<?> wrapper;
    private final Enchantment enchantment;
    private final int level;
    private final String operator;

    public EnchantmentData(Enchantment enchantment, int level, String operator) {
        this.enchantment = enchantment;
        this.level = level;
        this.operator = operator;
        this.wrapper = new ObjectWrapper<>(this);
    }

    public final Enchantment getEnchantment() {
        return this.enchantment;
    }

    public final int getLevel() {
        return this.level;
    }

    @Override
    public final String getOperator() {
        return this.operator;
    }

    @Override
    public final ObjectWrapper<?> getDataWrapper() {
        return this.wrapper;
    }
}
