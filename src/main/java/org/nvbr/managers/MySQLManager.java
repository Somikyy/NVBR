package org.nvbr.managers;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

public class MySQLManager {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private Connection connection;
    private Gson gson = new Gson();

    public MySQLManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        connection = DriverManager.getConnection("jdbc:mysql://" +
                this.host + ":" + this.port + "/" + this.database, this.username, this.password);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS ender_chests (player_id VARCHAR(36) NOT NULL, items TEXT NOT NULL, PRIMARY KEY (player_id))");
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Data
    public static class EnderChest {
        private UUID playerId;
        private Map<Integer, ItemStack> items;

        public EnderChest(UUID playerId, Map<Integer, ItemStack> items) {
            this.playerId = playerId;
            this.items = items;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void saveEnderChest(EnderChest enderChest) throws SQLException {
        String sql = "INSERT INTO ender_chests (player_id, items) VALUES (?, ?) ON DUPLICATE KEY UPDATE items = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enderChest.getPlayerId().toString());
            stmt.setString(2, gson.toJson(enderChest.getItems()));
            stmt.setString(3, gson.toJson(enderChest.getItems()));
            stmt.executeUpdate();
        }
    }

    public EnderChest loadEnderChest(UUID playerId) throws SQLException {
        String sql = "SELECT items FROM ender_chests WHERE player_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Type type = new TypeToken<Map<Integer, ItemStack>>() {
                }.getType();
                Map<Integer, ItemStack> items = gson.fromJson(rs.getString("items"), type);
                return new EnderChest(playerId, items);
            } else {
                return null;
            }
        }
    }
}