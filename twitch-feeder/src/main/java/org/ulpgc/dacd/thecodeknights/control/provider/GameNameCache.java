package org.ulpgc.dacd.thecodeknights.control.provider;

import java.sql.*;
import java.util.*;

public class GameNameCache {

    private final String dbPath;

    public GameNameCache(String dbPath) {
        this.dbPath = dbPath;
        init();
    }

    private void init() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS game_names (game_id TEXT PRIMARY KEY, name TEXT NOT NULL)");
        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize game name cache", e);
        }
    }

    public Map<String, String> getKnownNames(Set<String> gameIds) {
        Map<String, String> result = new HashMap<>();
        if (gameIds.isEmpty()) return result;
        String placeholders = String.join(",", Collections.nCopies(gameIds.size(), "?"));
        String sql = "SELECT game_id, name FROM game_names WHERE game_id IN (" + placeholders + ")";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            for (String id : gameIds) ps.setString(i++, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.put(rs.getString("game_id"), rs.getString("name"));
        } catch (SQLException ignored) {}
        return result;
    }

    public void saveNames(Map<String, String> gameNames) {
        if (gameNames.isEmpty()) return;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR IGNORE INTO game_names (game_id, name) VALUES (?, ?)")) {
            for (Map.Entry<String, String> entry : gameNames.entrySet()) {
                ps.setString(1, entry.getKey());
                ps.setString(2, entry.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ignored) {}
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }
}
