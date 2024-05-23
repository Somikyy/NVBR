package org.nvbr.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.nvbr.NVBR;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.nvbr.models.EnderChestData;
import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import org.bukkit.event.Listener;
@RequiredArgsConstructor
public class EnderChestSyncManager implements Listener {
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

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            Player player = (Player) event.getPlayer();
            saveEnderChest(player);
        }
    }
}