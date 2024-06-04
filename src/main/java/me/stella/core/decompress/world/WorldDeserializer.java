package me.stella.core.decompress.world;

import com.sun.istack.internal.NotNull;
import me.stella.CinnamonBukkit;
import me.stella.CinnamonUtils;
import me.stella.reflection.ObjectCaster;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;
import me.stella.support.craftbukkit.DynamicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WorldDeserializer {

    private static WorldAlgorithm algorithm = WorldAlgorithm.LEGACY;

    public static void setAlgorithm(WorldAlgorithm algorithm) {
        WorldDeserializer.algorithm = algorithm;
    }

    public static String getAlgorithmID() {
        assert isAlgorithmPresent();
        return algorithm.name();
    }

    public static WorldAlgorithm.DecompressionAlgorithm getAlgorithm() {
        assert isAlgorithmPresent();
        return algorithm.algorithm;
    }

    public static boolean isAlgorithmPresent() {
        return algorithm != null;
    }

    public static CompletableFuture<List<int[]>> readLoadedChunks(@NotNull World world) {
        return CompletableFuture.supplyAsync(() -> {
            File serverFolder = CinnamonBukkit.getMain().getDataFolder().getParentFile().getParentFile();
            File worldFolder = new File(serverFolder, world.getName());
            if(!worldFolder.exists())
                return new ArrayList<>();
            List<int[]> chunks = new ArrayList<>();
            WorldAlgorithm.DecompressionAlgorithm algorithm = WorldDeserializer.algorithm.getAlgorithm();
            File regionFile = new File(worldFolder, "region");
            if(!regionFile.exists())
                return new ArrayList<>();
            for(File region: Objects.requireNonNull(regionFile.listFiles())) {
                if(!region.isFile())
                    continue;
                String[] mcaSyntax = region.getName().split("\\.");
                int regionX = Integer.parseInt(mcaSyntax[1]); int regionZ = Integer.parseInt(mcaSyntax[2]);
                ObjectWrapper<?> parsedRegion = algorithm.buildRegionFile(region);
                int[] offsetData = algorithm.getOffsetData(parsedRegion);
                for(int x = 0; x < 32; x++) {
                    for(int z = 0; z < 32; z++) {
                        if(offsetData[x + (z * 32)] != 0)
                            chunks.add(new int[] { ((regionX << 5) + x), ((regionZ << 5) + z) });
                    }
                }
            }
            return chunks;
        });
    }

    public static CompletableFuture<ObjectWrapper<?>> readChunkData(@NotNull World world, int chunkX, int chunkZ) {
        return CompletableFuture.supplyAsync(() -> {
            File serverFolder = CinnamonBukkit.getMain().getDataFolder().getParentFile().getParentFile();
            File worldFolder = new File(serverFolder, world.getName());
            if(!worldFolder.exists())
                return null;
            try {
                ObjectWrapper<?> data = algorithm.algorithm.parseNBTData(worldFolder, chunkX, chunkZ);
                return data;
            } catch(Exception err) { err.printStackTrace(); }
            return null;
        });
    }

    public static CompletableFuture<Boolean> isEmptyChunk(@NotNull World world, int chunkX, int chunkZ) {
        return CompletableFuture.supplyAsync(() -> {
           ObjectWrapper<?> chunkData = readChunkData(world, chunkX, chunkZ).join();
           if(chunkData == null)
               return true;
           SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
           SupportFrame nbtTagList = ClassLibrary.getSupportFor("NBTTagList");
           ObjectWrapper<?> tileEntities = nbtTagCompound.invokeMethod("get",
                   chunkData.getObject(), "TileEntities");
           ObjectWrapper<?> entities = nbtTagCompound.invokeMethod("get",
                   chunkData.getObject(), "Entities");
           int sizeTile = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", tileEntities.getObject()).getObject());
           int sizeEntities = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", entities.getObject()).getObject());
           return (sizeTile < 1) && (sizeEntities < 1);
        });
    }

    public static CompletableFuture<Map<BlockPos, Inventory>> readChestData(@NotNull ObjectWrapper<?> chunkData) {
        return CompletableFuture.supplyAsync(() -> {
            SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
            SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
            SupportFrame nbtTagList = ClassLibrary.getSupportFor("NBTTagList");
            ObjectWrapper<?> tileEntities = nbtTagCompound.invokeMethod("get",
                    chunkData.getObject(), "TileEntities");
            int size = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", tileEntities.getObject()).getObject());
            Map<BlockPos, Inventory> chests = new HashMap<>();
            for(int i = 0; i < size; i++) {
                ObjectWrapper<?> entity = nbtTagList.invokeMethod("get", tileEntities.getObject(), i);
                String id = String.valueOf(nbtTagCompound.invokeMethod("getString",
                        entity.getObject(), "id").getObject());
                if(id.contains("chest")) {
                    ObjectWrapper<?> items = nbtTagCompound.invokeMethod("get",
                            tileEntities.getObject(), "Items");
                    int x = ObjectCaster.toInteger(nbtTagCompound.invokeMethod("getInt",
                            tileEntities.getObject(), "x").getObject());
                    int y = ObjectCaster.toInteger(nbtTagCompound.invokeMethod("getInt",
                            tileEntities.getObject(), "y").getObject());
                    int z = ObjectCaster.toInteger(nbtTagCompound.invokeMethod("getInt",
                            tileEntities.getObject(), "z").getObject());
                    int listSize = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", items.getObject()).getObject());
                    Map<Integer, ItemStack> slots = new HashMap<>();
                    for(int k = 0; k < listSize; k++) {
                        ObjectWrapper<?> item = nbtTagList.invokeMethod("get", items.getObject(), k);
                        int slot = ObjectCaster.toByte(nbtTagCompound.invokeMethod("getByte",
                                item.getObject(), "Slot").getObject());
                        ObjectWrapper<?> netStack = itemStack.getClassWrapper().newInstance(
                                new Class<?>[] { nbtTagCompound.getClassWrapper().getWrappingClass() }, item);
                        slots.put(slot, DynamicBukkit.toBukkitStack(netStack));
                    }
                    int chestSize = CinnamonUtils.getTileEntitySize(CinnamonUtils.getMaxInSet(slots.keySet()));
                    Inventory inventory = Bukkit.createInventory(null, chestSize, null);
                    slots.forEach((slot, item) -> inventory.setItem(slot, item.clone()));
                    chests.put(BlockPos.of(x, y, z), inventory);
                }
            }
            return chests;
        });
    }

    public static CompletableFuture<Map<BlockPos, ItemStack>> readItemFrames(@NotNull ObjectWrapper<?> chunkData) {
        return CompletableFuture.supplyAsync(() -> {
            SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
            SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
            SupportFrame nbtTagList = ClassLibrary.getSupportFor("NBTTagList");
            ObjectWrapper<?> Entities = nbtTagCompound.invokeMethod("get",
                    chunkData.getObject(), "Entities");
            int size = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", Entities.getObject()).getObject());
            Map<BlockPos, ItemStack> itemFrames = new HashMap<>();
            for(int i = 0; i < size; i++) {
                ObjectWrapper<?> entity = nbtTagList.invokeMethod("get", Entities.getObject(), i);
                String id = String.valueOf(nbtTagCompound.invokeMethod("getString",
                        entity.getObject(), "id").getObject());
                if(id.equals("minecraft:item_frame")) {
                    ObjectWrapper<?> item = nbtTagCompound.invokeMethod("get",
                            entity.getObject(), "Item");
                    int x = ObjectCaster.toInteger(nbtTagCompound.invokeMethod("getInt",
                            entity.getObject(), "x").getObject());
                    int y = ObjectCaster.toInteger(nbtTagCompound.invokeMethod("getInt",
                            entity.getObject(), "y").getObject());
                    int z = ObjectCaster.toInteger(nbtTagCompound.invokeMethod("getInt",
                            entity.getObject(), "z").getObject());
                    ObjectWrapper<?> netStack = itemStack.getClassWrapper().newInstance(
                            new Class<?>[] { nbtTagCompound.getClassWrapper().getWrappingClass() }, item);
                    itemFrames.put(BlockPos.of(x, y, z), DynamicBukkit.toBukkitStack(netStack));
                }
            }
            return itemFrames;
        });
    }
}
