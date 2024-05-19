package me.stella.core.storage.branches.config;

public class ChunkMap {

    private final boolean enabled;
    private final long interval;
    private final boolean excludeEmpty;

    public ChunkMap(boolean enabled, long interval, boolean excludeEmpty) {
        this.enabled = enabled;
        this.interval = interval;
        this.excludeEmpty = excludeEmpty;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final boolean excludeEmptyChunks() {
        return this.excludeEmpty;
    }

    public final long getInterval() {
        return this.interval;
    }

    public final long getIntervalSeconds() {
        return (long) Math.floor((double) this.interval / 1000.0D);
    }

}
