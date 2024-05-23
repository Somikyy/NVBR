package org.nvbr;

import org.bukkit.plugin.java.JavaPlugin;
import org.nvbr.commands.GlobalChatCommand;
import org.nvbr.managers.EnderChestSyncManager;
import org.nvbr.managers.GlobalChatManager;
import org.nvbr.managers.RedisManager;
import org.nvbr.listeners.EnderChestListener;

public class NVBR extends JavaPlugin {
    private RedisManager redisManager;
    private EnderChestSyncManager enderChestSyncManager;
    private GlobalChatManager globalChatManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.redisManager = new RedisManager(this);
        this.enderChestSyncManager = new EnderChestSyncManager(this, redisManager);
        this.globalChatManager = new GlobalChatManager(this, redisManager);

        getCommand("globalchat").setExecutor(new GlobalChatCommand(globalChatManager));

        getServer().getPluginManager().registerEvents(enderChestSyncManager, this);
    }

    @Override
    public void onDisable() {
        redisManager.disconnect();
    }
}