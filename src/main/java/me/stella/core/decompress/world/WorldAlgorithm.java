package me.stella.core.decompress.world;

import com.sun.istack.internal.NotNull;
import me.stella.reflection.ObjectCaster;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;

import java.io.File;

public enum WorldAlgorithm {

    LEGACY("v1_12_R1", new DecompressionAlgorithm() {
        @Override
        public ObjectWrapper<?> buildRegionFile(@NotNull File file) {
            SupportFrame regionFile = ClassLibrary.getSupportFor("RegionFile");
            return regionFile.getClassWrapper().newInstance(new Class<?>[]{ File.class }, file);
        }
        @Override
        public int[] getOffsetData(@NotNull ObjectWrapper<?> parsedRegion) {
            SupportFrame regionFile = ClassLibrary.getSupportFor("RegionFile");
            return ObjectCaster.toIntArray(regionFile.accessField("offset", parsedRegion).getObject());
        }

        @Override
        public ObjectWrapper<?> parseNBTData(@NotNull File worldDirectory, int chunkX, int chunkZ) {
            SupportFrame regionFileCache = ClassLibrary.getSupportFor("RegionFileCache");
            SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
            ObjectWrapper<?> data = regionFileCache.invokeStaticMethod("loadChunkData", worldDirectory, chunkX, chunkZ);
            return nbtTagCompound.invokeMethod("get", data, "Level");
        }
    });

    final DecompressionAlgorithm algorithm;
    final String lastOperationalVersion;
    WorldAlgorithm(String lastVer, DecompressionAlgorithm algorithm) {
        this.lastOperationalVersion = lastVer;
        this.algorithm = algorithm;
    }

    public static WorldAlgorithm[] algorithms() {
        return new WorldAlgorithm[]{ WorldAlgorithm.LEGACY };
    }

    public final String getLastOperationalVersion() {
        return this.lastOperationalVersion;
    }

    public final DecompressionAlgorithm getAlgorithm() {
        return this.algorithm;
    }

    public abstract static class DecompressionAlgorithm {
        public abstract ObjectWrapper<?> buildRegionFile(@NotNull File file);
        public abstract int[] getOffsetData(@NotNull ObjectWrapper<?> regionFile);
        public abstract ObjectWrapper<?> parseNBTData(@NotNull File worldDirectory, int chunkX, int chunkZ);
    }

}
