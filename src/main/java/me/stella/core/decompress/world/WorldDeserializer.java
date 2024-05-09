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

    private static WorldAlgorithm algorithm = null;

    public static void setAlgorithm(WorldAlgorithm algorithm) {
        WorldDeserializer.algorithm = algorithm;
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
            //SupportFrame regionFile = ClassLibrary.getSupportFor("RegionFile");
            List<int[]> chunks = new ArrayList<>();
            WorldAlgorithm.DecompressionAlgorithm algorithm = WorldDeserializer.algorithm.getAlgorithm();
            for(File region: Objects.requireNonNull(new File(worldFolder, "region").listFiles())) {
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
            return algorithm.algorithm.parseNBTData(worldFolder, chunkX, chunkZ);
        });
    }

    public static CompletableFuture<List<Inventory>> readChestData(@NotNull ObjectWrapper<?> chunkData) {
        return CompletableFuture.supplyAsync(() -> {
            SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
            SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
            SupportFrame nbtTagList = ClassLibrary.getSupportFor("NBTTagList");
            ObjectWrapper<?> tileEntities = nbtTagCompound.invokeMethod("get",
                    chunkData.getObject(), "TileEntities");
            int size = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", tileEntities.getObject()));
            List<Inventory> chests = new ArrayList<>();
            for(int i = 0; i < size; i++) {
                ObjectWrapper<?> entity = nbtTagList.invokeMethod("get", tileEntities.getObject(), i);
                String id = String.valueOf(nbtTagCompound.invokeMethod("getString",
                        entity.getObject(), "id").getObject());
                if(id.contains("chest")) {
                    ObjectWrapper<?> items = nbtTagCompound.invokeMethod("get",
                            tileEntities.getObject(), "Items");
                    int listSize = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", items.getObject()));
                    Map<Integer, ItemStack> slots = new HashMap<>();
                    for(int x = 0; x < listSize; x++) {
                        ObjectWrapper<?> item = nbtTagList.invokeMethod("get", items.getObject(), x);
                        int slot = ObjectCaster.toByte(nbtTagCompound.invokeMethod("getByte",
                                item.getObject(), "Slot"));
                        ObjectWrapper<?> netStack = itemStack.getClassWrapper().newInstance(
                                new Class<?>[] { nbtTagCompound.getClassWrapper().getWrappingClass() }, item);
                        slots.put(slot, DynamicBukkit.toBukkitStack(netStack));
                    }
                    int chestSize = CinnamonUtils.getTileEntitySize(CinnamonUtils.getMaxInSet(slots.keySet()));
                    Inventory inventory = Bukkit.createInventory(null, chestSize, null);
                    slots.forEach((slot, item) -> inventory.setItem(slot, item.clone()));
                    chests.add(inventory);
                }
            }
            return chests;
        });
    }

    public static CompletableFuture<List<ItemStack>> readItemFrames(@NotNull ObjectWrapper<?> chunkData) {
        return CompletableFuture.supplyAsync(() -> {
            SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
            SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
            SupportFrame nbtTagList = ClassLibrary.getSupportFor("NBTTagList");
            ObjectWrapper<?> tileEntities = nbtTagCompound.invokeMethod("get",
                    chunkData.getObject(), "Entities");
            int size = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", tileEntities.getObject()));
            List<ItemStack> itemFrames = new ArrayList<>();
            for(int i = 0; i < size; i++) {
                ObjectWrapper<?> entity = nbtTagList.invokeMethod("get", tileEntities.getObject(), i);
                String id = String.valueOf(nbtTagCompound.invokeMethod("getString",
                        entity.getObject(), "id").getObject());
                if(id.equals("minecraft:item_frame")) {
                    ObjectWrapper<?> item = nbtTagCompound.invokeMethod("get",
                            entity.getObject(), "Item");
                    ObjectWrapper<?> netStack = itemStack.getClassWrapper().newInstance(
                            new Class<?>[] { nbtTagCompound.getClassWrapper().getWrappingClass() }, item);
                    itemFrames.add(DynamicBukkit.toBukkitStack(netStack));
                }
            }
            return itemFrames;
        });
    }

}
