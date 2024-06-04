package me.stella.core.tasks.scanners;

import com.sun.istack.internal.NotNull;
import me.lucko.helper.promise.Promise;
import me.stella.CinnamonBukkit;
import me.stella.CinnamonTable;
import me.stella.core.decompress.world.BlockPos;
import me.stella.core.decompress.world.WorldAlgorithm;
import me.stella.core.decompress.world.WorldDeserializer;
import me.stella.core.tasks.background.AsyncChunkMapper;
import me.stella.core.tasks.scanners.io.ScanResult;
import me.stella.core.tasks.scanners.io.SearchQuery;
import me.stella.core.tasks.scanners.reporter.WorldScanReporter;
import me.stella.reflection.ObjectWrapper;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldTileScanner {

    public static Promise<List<ScanResult<BlockPos, Integer>>> scanFromChests(@NotNull SearchQuery query, World world, boolean forceScan) {
        AsyncChunkMapper chunkMapper = CinnamonTable.get(AsyncChunkMapper.class);
        ScheduledExecutorService nativeScheduler = CinnamonBukkit.getJavaScheduler();
        final WorldScanReporter reporter = new WorldScanReporter();
        final AtomicInteger atomicInteger = new AtomicInteger();
        Promise<List<int[]>> chunks = Promise.start()
                .thenApplyAsync(f -> {
                    if(forceScan) {
                        File serverFolder = CinnamonBukkit.getMain().getDataFolder().getParentFile().getParentFile();
                        File worldFolder = new File(serverFolder, world.getName());
                        if(!worldFolder.exists())
                            return new ArrayList<>();
                        List<int[]> chunkList = new ArrayList<>();
                        WorldAlgorithm.DecompressionAlgorithm algorithm = WorldDeserializer.getAlgorithm();
                        File[] regionFolder = new File(worldFolder, "region").listFiles();
                        assert regionFolder != null;
                        ScheduledFuture<?> p1Task = nativeScheduler.scheduleAtFixedRate(() -> {
                            runUpdate(atomicInteger, (regionFolder.length * 1024), reporter);
                        }, 0L, 20L, TimeUnit.MILLISECONDS);
                        for(File region: regionFolder) {
                            if(!region.isFile())
                                continue;
                            String[] mcaSyntax = region.getName().split("\\.");
                            int regionX = Integer.parseInt(mcaSyntax[1]); int regionZ = Integer.parseInt(mcaSyntax[2]);
                            ObjectWrapper<?> parsedRegion = algorithm.buildRegionFile(region);
                            int[] offsetData = algorithm.getOffsetData(parsedRegion);
                            for(int x = 0; x < 32; x++) {
                                for(int z = 0; z < 32; z++) {
                                    if(offsetData[x + (z * 32)] != 0) {
                                        chunkList.add(new int[]{((regionX << 5) + x), ((regionZ << 5) + z)});
                                        atomicInteger.incrementAndGet();
                                    }
                                }
                            }
                        }
                        p1Task.cancel(false);
                        return chunkList;
                    }
                    else return chunkMapper.getChunkCache().get(world);
                });
        return chunks.thenApplyAsync(chunkList -> {
           reporter.nextPhase(); atomicInteger.set(0);
           ScheduledFuture<?> phase2Task = nativeScheduler.scheduleAtFixedRate(() -> {
               runUpdate(atomicInteger, (chunkList.size()), reporter);
           }, 0L, 20L, TimeUnit.MILLISECONDS);
           Map<BlockPos, Inventory> deserializedInventories = new HashMap<>();
           for(int[] coordPair: chunkList) {
               ObjectWrapper<?> chunkData = WorldDeserializer.readChunkData(world, coordPair[0], coordPair[1]).join();
               WorldDeserializer.readChestData(chunkData).join().forEach((pos, chest) -> {
                   deserializedInventories.put(pos, chest);
                   atomicInteger.incrementAndGet();
               });
           }
           phase2Task.cancel(true); reporter.nextPhase();
           return deserializedInventories;
        }).thenApplyAsync(inventoryMap -> {
            ScheduledFuture<?> phase3Task = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicInteger, inventoryMap.size(), reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            List<ScanResult<BlockPos, Integer>> output = new ArrayList<>();
            inventoryMap.forEach((pos, chest) -> {
                output.add(new ScanResult<>(pos, ScannerUtils.performScan(query, chest, chest.getSize())));
            });
            phase3Task.cancel(true);
            return output;
        });
    }

    public static Promise<List<ScanResult<BlockPos, Integer>>> scanItemFrames(@NotNull SearchQuery query, World world, boolean forceRescan) {
        AsyncChunkMapper chunkMapper = CinnamonTable.get(AsyncChunkMapper.class);
        ScheduledExecutorService nativeScheduler = CinnamonBukkit.getJavaScheduler();
        final WorldScanReporter reporter = new WorldScanReporter();
        final AtomicInteger atomicInteger = new AtomicInteger();
        Promise<List<int[]>> chunks = Promise.start()
                .thenApplyAsync(f -> {
                    if(forceRescan) {
                        File serverFolder = CinnamonBukkit.getMain().getDataFolder().getParentFile().getParentFile();
                        File worldFolder = new File(serverFolder, world.getName());
                        if(!worldFolder.exists())
                            return new ArrayList<>();
                        List<int[]> chunkList = new ArrayList<>();
                        WorldAlgorithm.DecompressionAlgorithm algorithm = WorldDeserializer.getAlgorithm();
                        File[] regionFolder = new File(worldFolder, "region").listFiles();
                        assert regionFolder != null;
                        ScheduledFuture<?> p1Task = nativeScheduler.scheduleAtFixedRate(() -> {
                            runUpdate(atomicInteger, (regionFolder.length * 1024), reporter);
                        }, 0L, 20L, TimeUnit.MILLISECONDS);
                        for(File region: regionFolder) {
                            if(!region.isFile())
                                continue;
                            String[] mcaSyntax = region.getName().split("\\.");
                            int regionX = Integer.parseInt(mcaSyntax[1]); int regionZ = Integer.parseInt(mcaSyntax[2]);
                            ObjectWrapper<?> parsedRegion = algorithm.buildRegionFile(region);
                            int[] offsetData = algorithm.getOffsetData(parsedRegion);
                            for(int x = 0; x < 32; x++) {
                                for(int z = 0; z < 32; z++) {
                                    if(offsetData[x + (z * 32)] != 0) {
                                        chunkList.add(new int[]{((regionX << 5) + x), ((regionZ << 5) + z)});
                                        atomicInteger.incrementAndGet();
                                    }
                                }
                            }
                        }
                        p1Task.cancel(false);
                        return chunkList;
                    }
                    else return chunkMapper.getChunkCache().get(world);
                });
        return chunks.thenApplyAsync(chunkList -> {
            reporter.nextPhase(); atomicInteger.set(0);
            ScheduledFuture<?> phase2Task = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicInteger, (chunkList.size()), reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            Map<BlockPos, ItemStack> deserializedItemFrames = new HashMap<>();
            for(int[] coordPair: chunkList) {
                ObjectWrapper<?> chunkData = WorldDeserializer.readChunkData(world, coordPair[0], coordPair[1]).join();
                WorldDeserializer.readItemFrames(chunkData).join().forEach((pos, frameItem) -> {
                    deserializedItemFrames.put(pos, frameItem);
                    atomicInteger.incrementAndGet();
                });
            }
            phase2Task.cancel(true); reporter.nextPhase();
            return deserializedItemFrames;
        }).thenApplyAsync(frames -> {
            ScheduledFuture<?> phase3Task = nativeScheduler.scheduleAtFixedRate(() -> {
                runUpdate(atomicInteger, frames.size(), reporter);
            }, 0L, 20L, TimeUnit.MILLISECONDS);
            List<ScanResult<BlockPos, Integer>> output = new ArrayList<>();
            frames.forEach((pos, frame) -> {
                if(ScannerUtils.matchScanQuery(frame.clone(), query))
                    output.add(new ScanResult<>(pos, frame.getAmount()));
            });
            phase3Task.cancel(true);
            return output;
        });
    }

    private static void runUpdate(AtomicInteger counter, int pool, WorldScanReporter reporter) {
        reporter.setCompletion((double) counter.get() / pool);
    }

}
