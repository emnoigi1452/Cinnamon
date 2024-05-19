package me.stella.core.tasks.scanners.io;

public class ScanResult<K, V> {

    private final K source;

    public K getSource() {
        return this.source;
    }

    private final V value;

    public V getValue() {
        return this.value;
    }

    public ScanResult(K source, V value) {
        this.source = source;
        this.value = value;
    }

    public static <K, V> ScanResult<K, V> of(K source, V value) {
        return new ScanResult<>(source, value);
    }

}
