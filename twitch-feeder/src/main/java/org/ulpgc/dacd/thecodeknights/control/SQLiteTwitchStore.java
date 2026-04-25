package org.ulpgc.dacd.thecodeknights.control;

import org.ulpgc.dacd.thecodeknights.model.Stream;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class SQLiteTwitchStore implements TwitchStore {

    private final String dbUrl;

    public SQLiteTwitchStore(String dbUrl) {
        this.dbUrl = dbUrl;
        createTable();
    }

    @Override
    public void save(List<Stream> streams) {
        String sql = "INSERT INTO twitch_streams(stream_id, user_name, game_id, title, viewer_count, captured_at) VALUES(?,?,?,?,?,?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String capturedAt = LocalDateTime.now().toString();

            for (Stream stream : streams) {
                stmt.setString(1, stream.getId());
                stmt.setString(2, stream.getUserName());
                stmt.setString(3, stream.getGameId());
                stmt.setString(4, stream.getTitle());
                stmt.setInt(5, stream.getViewerCount());
                stmt.setString(6, capturedAt);

                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Datos de Twitch guardados correctamente en SQLite.");

        } catch (SQLException e) {
            System.err.println("Error guardando datos: " + e.getMessage());
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS twitch_streams (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "stream_id TEXT," +
                "user_name TEXT," +
                "game_id TEXT," +
                "title TEXT," +
                "viewer_count INTEGER," +
                "captured_at TEXT" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error creando tabla: " + e.getMessage());
        }
    }
}