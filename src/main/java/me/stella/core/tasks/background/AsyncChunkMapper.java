package me.stella.core.tasks.background;

import com.google.common.collect.ImmutableMap;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.scheduler.Task;
import me.stella.CinnamonUtils;
import me.stella.core.decompress.world.WorldDeserializer;
import org.bukkit.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AsyncChunkMapper {

    private Task mapper;

    public Task getMapperTask() {
        return this.mapper;
    }

    private Map<World, List<int[]>> cache = new HashMap<>();

    public Map<World, List<int[]>> getChunkCache() {
        return ImmutableMap.copyOf(this.cache);
    }

    private boolean enabled = false;
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    private long interval = 72000L;
    
    public long getInterval() {
        return this.interval;
    }

    private boolean filterEmpty = true;

    public boolean excludeEmptyChunks() {
        return this.filterEmpty;
    }

    public void load(boolean enabled, long intervalTicks, boolean filterEmpty) {
        this.enabled = enabled;
        this.interval = intervalTicks;
        this.filterEmpty = filterEmpty;
        runPromise();
    }

    private void runPromise() {
        // if another mapper is active, close that shit
        if(this.mapper != null)
            mapper.close();
        // start running the task
        mapper = Schedulers.async().runRepeating(() -> {
            // clear current chunks being cache
            cache = Collections.synchronizedMap(new HashMap<>());
            try {
                // obtaining loaded worlds via server main thread
                Promise.start().thenApplySync(e -> CinnamonUtils.getServer().getWorlds()).thenApplyAsync(worlds -> {
                    // we run through each world
                            worlds.forEach(world -> {
                                // get loaded chunks via the deserialization process
                                Promise<List<int[]>> chunks = Promise.start()
                                        .thenApplyAsync(c -> WorldDeserializer.readLoadedChunks(world).join())
                                        // if enabled, filters out empty chunks (no entities/chunks)
                                        .thenApplyAsync(coords -> coords.stream().filter(pair -> {
                                            if(!excludeEmptyChunks())
                                                return true;
                                            return !(WorldDeserializer.isEmptyChunk(world, pair[0], pair[1])).join();
                                        }).collect(Collectors.toList()));
                                // put the rest in the cache, idk
                                chunks.thenAcceptSync(c -> cache.put(world, c));
                            });
                            return null;
                        });
            } catch(Exception err) { err.printStackTrace(); }
        }, 100L, getInterval());
    }


}
