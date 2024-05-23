package org.nvbr.managers;

import org.nvbr.NVBR;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
    private JedisPool jedisPool;
    private NVBR plugin;

    public RedisManager(NVBR plugin) {
        this.plugin = plugin;
        String redisHost = plugin.getConfig().getString("redis.host");
        int redisPort = plugin.getConfig().getInt("redis.port");
        this.jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void disconnect() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}