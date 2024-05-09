package me.stella.core.storage.branches.config;

public class UUIDMap {

    private final boolean enabled;
    private final long interval;

    public UUIDMap(boolean enabled, long interval) {
        this.enabled = enabled;
        this.interval = interval;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final long getInterval() {
        return this.interval;
    }

    public final long getIntervalSeconds() {
        return (long) Math.floor((double) this.interval / 1000.0D);
    }

}
