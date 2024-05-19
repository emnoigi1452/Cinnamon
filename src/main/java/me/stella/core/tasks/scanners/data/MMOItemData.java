package me.stella.core.tasks.scanners.data;

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
