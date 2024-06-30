package me.stella.core.tasks.scanners;

import com.sun.istack.internal.NotNull;
import me.lucko.helper.promise.Promise;
import me.stella.CinnamonBukkit;
import me.stella.core.decompress.world.BlockPos;
import me.stella.core.decompress.world.WorldDeserializer;
import me.stella.core.tasks.background.AsyncChunkMapper;
import me.stella.core.tasks.scanners.io.ScanResult;
import me.stella.core.tasks.scanners.io.SearchQuery;
import me.stella.objects.AutoCloseJavaScheduler;
import me.stella.objects.CinnamonTable;
import me.stella.objects.MultiThreadExecutor;
import me.stella.objects.reporter.impl.WorldScanReporter;
import me.stella.reflection.ObjectWrapper;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldTileScanner {

    public static Promise<List<ScanResult<BlockPos, Integer>>> scanFromChests(@NotNull SearchQuery query, World world, boolean forceScan) {
        AsyncChunkMapper chunkMapper = CinnamonTable.get(AsyncChunkMapper.class);
        AutoCloseJavaScheduler nativeScheduler = CinnamonBukkit.getJavaScheduler();
        final WorldScanReporter reporter = new WorldScanReporter();
        Promise<List<int[]>> chunks = Promise.start()
                .thenApplyAsync(f -> {
                    if(forceScan)
                        return WorldDeserializer.readLoadedChunks(world).join();
                    else return chunkMapper.getChunkCache().get(world);
                });
        return chunks.thenApplyAsync(chunkList -> {
            MultiThreadExecutor executor = new MultiThreadExecutor(128);
            final AtomicInteger atomicInteger = executor.getCounter();
            reporter.nextPhase();
            ScheduledFuture<?> phase2Task = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicInteger, (chunkList.size()), reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            Map<BlockPos, Inventory> deserializedInventories = new HashMap<>();
            try {
                for(int[] coordPair: chunkList) {
                    executor.queueTask(() -> {
                        ObjectWrapper<?> chunkData = WorldDeserializer.readChunkData(world, coordPair[0], coordPair[1]).join();
                        WorldDeserializer.readChestData(chunkData).join().forEach((pos, chest) -> {
                            deserializedInventories.put(pos, chest);
                            atomicInteger.incrementAndGet();
                        });
                        return null;
                    });
                }
                executor.awaitCompletion();
            } catch(Exception err) { err.printStackTrace(); }
            phase2Task.cancel(true); reporter.nextPhase(); System.gc();
            return deserializedInventories;
        }).thenApplyAsync(inventoryMap -> {
            MultiThreadExecutor executor = new MultiThreadExecutor(128);
            AtomicInteger atomicInteger = executor.getCounter();
            ScheduledFuture<?> phase3Task = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicInteger, inventoryMap.size(), reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            List<ScanResult<BlockPos, Integer>> output = new ArrayList<>();
            try {
                inventoryMap.forEach((pos, chest) -> {
                    executor.queueTask(() -> output.add(new ScanResult<>(pos, ScannerUtils.performScan(query, chest, chest.getSize()))));
                });
                executor.awaitCompletion();
            } catch(Exception err) { err.printStackTrace(); }
            phase3Task.cancel(true); System.gc();
            return output;
        });
    }

    public static Promise<List<ScanResult<BlockPos, Integer>>> scanItemFrames(@NotNull SearchQuery query, World world, boolean forceScan) {
        AsyncChunkMapper chunkMapper = CinnamonTable.get(AsyncChunkMapper.class);
        AutoCloseJavaScheduler nativeScheduler = CinnamonBukkit.getJavaScheduler();
        final WorldScanReporter reporter = new WorldScanReporter();
        Promise<List<int[]>> chunks = Promise.start()
                .thenApplyAsync(f -> {
                    if(forceScan)
                        return WorldDeserializer.readLoadedChunks(world).join();
                    else return chunkMapper.getChunkCache().get(world);
                });
        return chunks.thenApplyAsync(chunkList -> {
            MultiThreadExecutor executor = new MultiThreadExecutor(128);
            AtomicInteger atomicInteger = executor.getCounter();
            reporter.nextPhase(); atomicInteger.set(0);
            ScheduledFuture<?> phase2Task = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicInteger, (chunkList.size()), reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            Map<BlockPos, ItemStack> deserializedItemFrames = new HashMap<>();
            try {
                for(int[] coordPair: chunkList) {
                    executor.queueTask(() -> {
                        ObjectWrapper<?> chunkData = WorldDeserializer.readChunkData(world, coordPair[0], coordPair[1]).join();
                        WorldDeserializer.readItemFrames(chunkData).join().forEach((pos, frameItem) -> {
                            deserializedItemFrames.put(pos, frameItem);
                            atomicInteger.incrementAndGet();
                        });
                        return null;
                    });
                }
                executor.awaitCompletion();
            } catch(Exception err) { err.printStackTrace(); }
            phase2Task.cancel(true); reporter.nextPhase(); System.gc();
            return deserializedItemFrames;
        }).thenApplyAsync(frames -> {
            MultiThreadExecutor executor = new MultiThreadExecutor(128);
            AtomicInteger atomicInteger = executor.getCounter();
            ScheduledFuture<?> phase3Task = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicInteger, frames.size(), reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            List<ScanResult<BlockPos, Integer>> output = new ArrayList<>();
            try {
                frames.forEach((pos, frame) -> {
                    executor.queueTask(() -> {
                        if(ScannerUtils.matchScanQuery(frame.clone(), query))
                            output.add(new ScanResult<>(pos, frame.getAmount()));
                        return null;
                    });
                });
                executor.awaitCompletion();
            } catch(Exception err) { err.printStackTrace(); }
            phase3Task.cancel(true); System.gc();
            return output;
        });
    }

    private static void runUpdate(AtomicInteger counter, int pool, WorldScanReporter reporter) {
        reporter.setCompletion((double) counter.get() / pool);
    }

}
