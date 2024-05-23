package org.nvbr.managers;

import org.nvbr.NVBR;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
public class GlobalChatManager {
    private final NVBR plugin;
    private final RedisManager redisManager;

    public void sendMessage(Player sender, String message) {
        try (Jedis jedis = redisManager.getJedis()) {
            String formattedMessage = String.format("[%s] %s: %s", plugin.getConfig().getString("server.name"), sender.getName(), message);
            jedis.publish("globalchat", formattedMessage);
        }
    }

    public void receiveMessages() {
        new Thread(() -> {
            try (Jedis jedis = redisManager.getJedis()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if ("globalchat".equals(channel)) {
                            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
                        }
                    }
                }, "globalchat");
            }
        }).start();
    }
}