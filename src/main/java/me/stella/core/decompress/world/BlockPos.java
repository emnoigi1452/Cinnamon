package me.stella.core.decompress.world;

public class BlockPos {

    private final int x;
    private final int y;
    private final int z;

    protected BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockPos of(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    public final int x() {
        return this.x;
    }

    public final int y() {
        return this.y;
    }

    public final int z() {
        return this.z;
    }

}
