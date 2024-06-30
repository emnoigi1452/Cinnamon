package me.stella.core.tasks.scanners;

import com.google.common.collect.ImmutableMap;
import com.sun.istack.internal.NotNull;
import me.lucko.helper.promise.Promise;
import me.stella.CinnamonBukkit;
import me.stella.core.decompress.PlayerDataDeserializer;
import me.stella.core.tasks.background.AsyncUUIDMapper;
import me.stella.core.tasks.scanners.io.ScanResult;
import me.stella.core.tasks.scanners.io.SearchQuery;
import me.stella.objects.AutoCloseJavaScheduler;
import me.stella.objects.CinnamonTable;
import me.stella.objects.reporter.impl.PlayerScanReporter;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerContainerScanner {

    // what the actual fuck is that return type
    public static Promise<List<ScanResult<ContainerSource, Integer>>> scan(@NotNull SearchQuery query, boolean forceRemap) {
        Promise<Map<UUID, String>> task;
        AsyncUUIDMapper uidMapper = CinnamonTable.get(AsyncUUIDMapper.class);
        // attempts a remap of all user's uid
        final PlayerScanReporter reporter = new PlayerScanReporter();
        final AtomicInteger atomicCounter = new AtomicInteger();
        final AutoCloseJavaScheduler nativeScheduler = CinnamonBukkit.getJavaScheduler();
        if(forceRemap) {
            final SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
            task = Promise.start().thenApplySync(i -> Arrays.asList(Bukkit.getOfflinePlayers()))
                    .thenApplyAsync(o -> {
                        int size = o.size();
                        ScheduledFuture<?> phase1Counter = nativeScheduler.scheduleAtFixedRate(() -> {
                            runUpdate(atomicCounter, size, reporter);
                        }, 0L, 20L, TimeUnit.MILLISECONDS);
                        Map<UUID, String> cache = Collections.synchronizedMap(new HashMap<>());
                        o.stream().map(op -> {
                            ObjectWrapper<?> playerData = PlayerDataDeserializer.readPlayerData(op.getUniqueId()).join();
                            ObjectWrapper<?> bukkitAttribute = nbtTagCompound.invokeMethod("get",
                                    playerData, "bukkit");
                            String name = String.valueOf(nbtTagCompound.invokeMethod("get",
                                    bukkitAttribute, "lastKnownName"));
                            atomicCounter.incrementAndGet();
                            return new Object[] { op.getUniqueId(), name };
                        }).forEach(entry -> cache.put(UUID.fromString(String.valueOf(entry[0])), String.valueOf(entry[1])));
                        phase1Counter.cancel(true);
                        return ImmutableMap.copyOf(cache);
                    });
        } else task = Promise.start().thenApplyAsync(n -> uidMapper.getUIDCache());
        // start scanning uwu
        return task.thenApplyAsync(uidMap -> {
            // mapping encoded userdata in server's repository
            int size = uidMap.size(); atomicCounter.set(0); reporter.nextPhase();
            ScheduledFuture<?> phase2Counter = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicCounter, size, reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            Map<String, ObjectWrapper<?>> playerData = new HashMap<>();
            uidMap.forEach((uid, name) -> {
                ObjectWrapper<?> internalData = PlayerDataDeserializer.readPlayerData(uid).join();
                playerData.put(name, internalData); atomicCounter.incrementAndGet();
            });
            phase2Counter.cancel(true); reporter.nextPhase();
            return playerData;
        }).thenApplyAsync(playerDataMap -> {
            // i have no idea what i was cooking here, but it works (i think)
            int size = playerDataMap.size(); atomicCounter.set(0);
            ScheduledFuture<?> phase3Counter = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicCounter, size, reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            List<ScanResult<ContainerSource, Integer>> output = new ArrayList<>();
            playerDataMap.forEach((player, data) -> {
                // scanning inventory
                Inventory inventory = PlayerDataDeserializer.buildInventory(data).join();
                ContainerSource sourceInv = new ContainerSource(uidMapper.getUUIDByName(player), InternalContainer.INVENTORY);
                output.add(new ScanResult<>(sourceInv, ScannerUtils.performScan(query, inventory, 41)));
                // scanning ender chest
                Inventory enderChest = PlayerDataDeserializer.buildEnderChest(data).join();
                ContainerSource sourceEC = new ContainerSource(uidMapper.getUUIDByName(player), InternalContainer.ENDER_CHEST);
                output.add(new ScanResult<>(sourceEC, ScannerUtils.performScan(query, enderChest, 27)));
                atomicCounter.incrementAndGet();
                // garbage cleaner to cleanup junk data (pls no memory leak)
                try {
                    System.gc();
                } catch(Exception err) { err.printStackTrace(); }
            });
            phase3Counter.cancel(true);
            return output;
        });
    }

    private static void runUpdate(AtomicInteger counter, int pool, PlayerScanReporter reporter) {
        reporter.setCompletion((double) counter.get() / pool);
    }

    public static class ContainerSource {
        private final InternalContainer type;
        private final UUID host;

        public ContainerSource(UUID uid, InternalContainer container) {
            this.host = uid;
            this.type = container;
        }

        public UUID getHost() {
            return this.host;
        }

        public InternalContainer getType() {
            return this.type;
        }
    }

    public enum InternalContainer {
        ENDER_CHEST, INVENTORY
    }

}
