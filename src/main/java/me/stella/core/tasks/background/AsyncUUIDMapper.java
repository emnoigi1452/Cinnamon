package me.stella.core.tasks.background;

import com.google.common.collect.ImmutableMap;
import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.scheduler.Task;
import me.stella.core.decompress.PlayerDataDeserializer;
import me.stella.reflection.ObjectWrapper;
import me.stella.support.ClassLibrary;
import me.stella.support.SupportFrame;
import org.bukkit.Bukkit;

import java.util.*;

public class AsyncUUIDMapper {

    private Task mapper;

    public Task getMapperTask() {
        return this.mapper;
    }

    private Map<UUID, String> cache = new HashMap<>();

    public Map<UUID, String> getUIDCache() {
        return ImmutableMap.copyOf(this.cache);
    }

    public String getNameByUUID(UUID uid) {
        return this.cache.get(uid);
    }

    public UUID getUUIDByName(String name) {
        return this.cache.entrySet().stream()
                .filter(entry -> entry.getValue().equals(name))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    private boolean enabled = false;

    public boolean isEnabled() {
        return this.enabled;
    }

    private long interval = 72000L;

    public long getInterval() {
        return this.interval;
    }


    public void load(boolean enabled, long interval) {
        this.enabled = enabled;
        this.interval = interval;
        runPromise();
    }

    private void runPromise() {
        if(this.mapper != null)
            mapper.close();
        mapper = Schedulers.async().runRepeating(() -> {
            try {
                final SupportFrame nbtTagCompound = ClassLibrary.getSupportFor("NBTTagCompound");
                cache = Collections.synchronizedMap(new HashMap<>());
                Promise.start().thenApplySync(i -> Arrays.asList(Bukkit.getOfflinePlayers()))
                        .thenApplyAsync(o -> {
                            o.stream().map(op -> {
                                ObjectWrapper<?> playerData = PlayerDataDeserializer.readPlayerData(op.getUniqueId()).join();
                                ObjectWrapper<?> bukkitAttribute = nbtTagCompound.invokeMethod("get",
                                        playerData, "bukkit");
                                String name = String.valueOf(nbtTagCompound.invokeMethod("get",
                                        bukkitAttribute, "lastKnownName"));
                                return new Object[] { op.getUniqueId(), name };
                            }).forEach(entry -> cache.put(UUID.fromString(String.valueOf(entry[0])), String.valueOf(entry[1])));
                            return null;
                        });
            } catch(Exception err) { err.printStackTrace(); }
        }, 100L, getInterval());
    }

}
