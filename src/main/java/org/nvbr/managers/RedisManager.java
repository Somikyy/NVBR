package org.nvbr.managers;

import org.nvbr.NVBR;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class RedisManager {
    private JedisPool pool;
    private RedissonClient client;
    private NVBR plugin;

    public RedisManager(NVBR plugin) {
        this.plugin = plugin;
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + plugin.getConfig().getString("redis.host") + ":" + plugin.getConfig().getInt("redis.port"));
        this.client = Redisson.create(config);

        initPubSub();
    }

    private void initPubSub() {
        client.getTopic("globalchat").addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String message) {
                handleMessage(channel.toString(), message);
            }
        });
    }

    private void handleMessage(String channel, String message) {
        if ("globalchat".equals(channel)) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                });
            });
        }
    }

    public void sendMessage(String message) {
        client.getTopic("globalchat").publish(message);
    }

    public Jedis getJedis() {
        if (pool == null) {
            pool = new JedisPool(new JedisPoolConfig(), "localhost");
        }
        return pool.getResource();
    }

    public void disconnect() {
        if (client != null) {
            client.shutdown();
        }
    }
}