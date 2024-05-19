package me.stella.core.tasks.scanners.data;

public interface ScopeBasedData {

    String getTarget();

    default boolean isGlobal() {
        return !getTarget().isEmpty();
    }

}
