package me.stella.core.decompress;

import com.sun.istack.internal.NotNull;
import me.stella.CinnamonBukkit;
import me.stella.reflection.ObjectCaster;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;
import me.stella.support.craftbukkit.DynamicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataDeserializer {

    public static CompletableFuture<ObjectWrapper<?>> readPlayerData(@NotNull UUID uid) {
        Server server = Bukkit.getServer();
        World worldMain = server.getWorlds().get(0);
        File serverFolder = CinnamonBukkit.getMain().getDataFolder().getParentFile().getParentFile();
        File playerDataFile = new File(serverFolder,
                worldMain.getName() + "/playerdata/" + uid.toString().concat(".dat"));
        return readPlayerData(playerDataFile);
    }

    public static CompletableFuture<ObjectWrapper<?>> readPlayerData(@NotNull File playerDataFile) {
        final SupportFrame nbtCompressedStreamTools = ClassLibrary.getSupportFor("NBTCompressedStreamTools");
        return CompletableFuture.supplyAsync(() -> {
            if(!playerDataFile.exists())
                return null;
            else return nbtCompressedStreamTools.invokeStaticMethod("readNBT", playerDataFile);
        });
    }

    public static CompletableFuture<Inventory> buildInventory(@NotNull Object playerData) {
        return buildInventory(new ObjectWrapper<>(playerData));
    }

    public static CompletableFuture<Inventory> buildInventory(@NotNull ObjectWrapper<?> wrapper) {
        final SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
        final SupportFrame nbtTagList = ClassLibrary.getSupportFor("NBTTagList");
        final SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
        return CompletableFuture.supplyAsync(() -> {
            Inventory inventory = Bukkit.createInventory(null, 45, "");
            ObjectWrapper<?> inventoryData = nbtTagCompound.invokeMethod("get", wrapper, "Inventory");
            int size = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", inventoryData).getObject());
            for(int x = 0; x < size; x++) {
                ObjectWrapper<?> slotData = nbtTagList.invokeMethod("get", inventoryData, x);
                int slot = (ObjectCaster.toByte(nbtTagCompound.invokeMethod("getByte", slotData, "Slot").getObject()) & 0xFF);
                ObjectWrapper<?> netStack = itemStack.getClassWrapper().newInstance(
                        new Class<?>[] { nbtTagCompound.getClassWrapper().getWrappingClass() }, slotData);
                if(ObjectCaster.toBoolean(itemStack.invokeMethod("isEmpty", netStack).getObject()))
                    continue;
                if(slot <= 35)
                    inventory.setItem(slot, DynamicBukkit.toBukkitStack(netStack));
                else if(slot >= 100 && slot <= 103)
                    inventory.setItem(39 - (slot - 100), DynamicBukkit.toBukkitStack(netStack));
                else
                    inventory.setItem(40, DynamicBukkit.toBukkitStack(netStack));
            }
            return inventory;
        });
    }



    public static CompletableFuture<Inventory> buildEnderChest(@NotNull Object playerData) {
        return buildEnderChest(new ObjectWrapper<>(playerData));
    }

    public static CompletableFuture<Inventory> buildEnderChest(@NotNull ObjectWrapper<?> wrapper) {
        final SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
        final SupportFrame nbtTagList = ClassLibrary.getSupportFor("NBTTagList");
        final SupportFrame itemStack = ClassLibrary.getSupportFor("ItemStack");
        return CompletableFuture.supplyAsync(() -> {
            Inventory inventory = Bukkit.createInventory(null, 27, "");
            ObjectWrapper<?> inventoryData = nbtTagCompound.invokeMethod("get", wrapper, "EnderItems");
            int size = ObjectCaster.toInteger(nbtTagList.invokeMethod("size", inventoryData).getObject());
            for(int x = 0; x < size; x++) {
                ObjectWrapper<?> slotData = nbtTagList.invokeMethod("get", inventoryData, x);
                int slot = (ObjectCaster.toByte(nbtTagCompound.invokeMethod("getByte", slotData, "Slot").getObject()) & 0xFF);
                ObjectWrapper<?> netStack = itemStack.getClassWrapper().newInstance(
                        new Class<?>[] { nbtTagCompound.getClassWrapper().getWrappingClass() }, slotData);
                if(ObjectCaster.toBoolean(itemStack.invokeMethod("isEmpty", netStack).getObject()))
                    continue;
                if(slot >= 0 && slot <= 26)
                    inventory.setItem(slot, DynamicBukkit.toBukkitStack(netStack));
            }
            return inventory;
        });
    }

}
