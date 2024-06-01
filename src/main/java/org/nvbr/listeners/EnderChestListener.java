package org.nvbr.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.data.type.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.nvbr.managers.EnderChestSyncManager;
import org.nvbr.managers.MySQLManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EnderChestListener implements Listener {
    private final EnderChestSyncManager enderChestSyncManager;
    private final MySQLManager mySQLManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            MySQLManager.EnderChest enderChest = mySQLManager.loadEnderChest(player.getUniqueId());
            if (enderChest != null) {
                player.getEnderChest().setContents(enderChest.getItems().values().toArray(new ItemStack[0]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() == event.getPlayer().getEnderChest()) {
            Player player = (Player) event.getPlayer();
            Map<Integer, ItemStack> items = new HashMap<>();
            ItemStack[] contents = player.getEnderChest().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null) {
                    items.put(i, contents[i]);
                }
            }
            MySQLManager.EnderChest enderChest = new MySQLManager.EnderChest(player.getUniqueId(), items);
            try {
                mySQLManager.saveEnderChest(enderChest);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}