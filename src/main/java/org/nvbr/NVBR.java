package org.nvbr;

import org.nvbr.managers.MySQLManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.nvbr.commands.GlobalChatCommand;
import org.nvbr.managers.EnderChestSyncManager;
import org.nvbr.managers.GlobalChatManager;
import org.nvbr.managers.RedisManager;

import java.sql.SQLException;

public class NVBR extends JavaPlugin {
    private RedisManager redisManager;
    private EnderChestSyncManager enderChestSyncManager;
    private GlobalChatManager globalChatManager;
    private MySQLManager mySQLManager;

    @Override
    public void onEnable() {
        mySQLManager = new MySQLManager("host", 3306, "database", "username", "password");
        try {
            mySQLManager.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            // Disable the plugin if we can't connect to the MySQL database
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.saveDefaultConfig();
        this.redisManager = new RedisManager(this);
        this.enderChestSyncManager = new EnderChestSyncManager(mySQLManager);
        this.globalChatManager = new GlobalChatManager(this, redisManager);

        getCommand("globalchat").setExecutor(new GlobalChatCommand(globalChatManager));

        EnderChestSyncManager enderChestSyncManager = new EnderChestSyncManager(mySQLManager);
        getServer().getPluginManager().registerEvents(enderChestSyncManager, this);
    }

    @Override
    public void onDisable() {
        try {
            mySQLManager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        redisManager.disconnect();
    }
}