package me.stella.core.tasks.scanners.data;

public class LockedItemData implements ScopeBasedData {

    private final String owner;

    public LockedItemData() {
        this("");
    }

    public LockedItemData(String owner) {
        this.owner = owner;
    }

    @Override
    public final String getTarget() {
        return this.owner;
    }
}
