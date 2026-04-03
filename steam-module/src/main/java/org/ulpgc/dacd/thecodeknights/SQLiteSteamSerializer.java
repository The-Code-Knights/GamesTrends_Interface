package org.ulpgc.dacd.thecodeknights;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class SQLiteSteamSerializer implements SteamSerializerInterface {

    private static final String DB_URL = "jdbc:sqlite:steam.db";

    public SQLiteSteamSerializer() {
        createTable();
    }

    @Override
    public void save(List<SteamGame> games) {
        String sql = "INSERT INTO steam_games(app_id, name, players, captured_at) VALUES(?,?,?,?)"; //rank despues de name

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String capturedAt = LocalDateTime.now().toString();

            for (SteamGame game : games) {
                stmt.setString(1, game.appId);
                stmt.setString(2, game.name);
                stmt.setInt(3, game.currentPlayers);
                stmt.setString(4, capturedAt);

                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Datos guardados correctamente en SQLite.");

        } catch (SQLException e) {
            System.err.println("Error guardando datos: " + e.getMessage());
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS steam_games (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "app_id TEXT," +
                "name TEXT," +
                //"rank INTEGER," +
                "players INTEGER," +
                "captured_at TEXT" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error creando tabla: " + e.getMessage());
        }
    }
    //Para testear
    public void printAllGames() {
        String sql = "SELECT app_id, name, players, captured_at FROM steam_games"; //rank

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("---- DATOS EN SQLITE ----");

            while (rs.next()) {
                System.out.println(
                        "AppID: " + rs.getString("app_id") +
                                " | Name: " + rs.getString("name") +
                                //" | Rank: " + rs.getInt("rank") +
                                " | Players: " + rs.getInt("players") +
                                " | CapturedAt: " + rs.getString("captured_at")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error leyendo datos: " + e.getMessage());
        }
    }
}