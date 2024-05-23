package org.nvbr.managers;

import org.nvbr.NVBR;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.nvbr.models.EnderChestData;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;

@RequiredArgsConstructor
public class EnderChestSyncManager {
    private final NVBR plugin;
    private final RedisManager redisManager;
    private final Gson gson = new Gson();

    public void saveEnderChest(Player player) {
        try (Jedis jedis = redisManager.getJedis()) {
            EnderChestData data = new EnderChestData(player.getEnderChest().getContents());
            jedis.set("enderchest:" + player.getUniqueId(), gson.toJson(data));
        }
    }

    public void loadEnderChest(Player player) {
        try (Jedis jedis = redisManager.getJedis()) {
            String json = jedis.get("enderchest:" + player.getUniqueId());
            if (json != null) {
                EnderChestData data = gson.fromJson(json, EnderChestData.class);
                player.getEnderChest().setContents(data.getContents());
            }
        }
    }
}