package org.nvbr.managers;

import org.nvbr.NVBR;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GlobalChatManager {
    private final NVBR plugin;
    private final RedisManager redisManager;

    public void sendMessage(Player sender, String message) {
        String formattedMessage = String.format("[%s] %s: %s",
                plugin.getConfig().getString("server.name"),
                sender.getName(),
                ChatColor.translateAlternateColorCodes('&', message)
        );
        redisManager.sendMessage(formattedMessage);
    }
}