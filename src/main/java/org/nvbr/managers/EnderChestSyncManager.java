package org.nvbr.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.nvbr.managers.MySQLManager.EnderChest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderChestSyncManager implements Listener {
    private MySQLManager mySQLManager;

    public EnderChestSyncManager(MySQLManager mySQLManager) {
        this.mySQLManager = mySQLManager;
        startSyncTask();
    }

    private void startSyncTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("NVBR"), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                savePlayerEnderChest(player);
            }
        }, 0L, 20L);
    }

    private void savePlayerEnderChest(Player player) {
        UUID playerId = player.getUniqueId();
        Map<Integer, ItemStack> items = new HashMap<>();
        ItemStack[] contents = player.getEnderChest().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                items.put(i, contents[i]);
            }
        }
        EnderChest enderChest = new EnderChest(playerId, items);
        try {
            mySQLManager.saveEnderChest(enderChest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        try {
            EnderChest enderChest = mySQLManager.loadEnderChest(playerId);
            if (enderChest != null) {
                event.getPlayer().getEnderChest().setContents(enderChest.getItems().values().toArray(new ItemStack[0]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        savePlayerEnderChest(event.getPlayer());
    }
}