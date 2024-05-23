package org.nvbr.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.data.type.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.nvbr.managers.EnderChestSyncManager;

@RequiredArgsConstructor
public class EnderChestListener implements Listener {
    private final EnderChestSyncManager enderChestSyncManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        enderChestSyncManager.loadEnderChest(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof EnderChest) {
            enderChestSyncManager.saveEnderChest((Player) event.getPlayer());
        }
    }
}