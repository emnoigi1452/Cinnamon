package me.stella.core.tasks.scanners.data.impl;

import me.stella.core.tasks.scanners.data.ScopeBasedData;

public class MMOItemData implements ScopeBasedData {

    private final String id;

    public MMOItemData() {
        this("");
    }

    public MMOItemData(String id) {
        this.id = id;
    }

    @Override
    public String getTarget() {
        return this.id;
    }
}
