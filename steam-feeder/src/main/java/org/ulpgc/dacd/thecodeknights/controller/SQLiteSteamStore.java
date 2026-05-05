/*
package org.ulpgc.dacd.thecodeknights.controller;

import org.ulpgc.dacd.thecodeknights.model.SteamGame;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class SQLiteSteamStore implements SteamStore {

    private final String dbURL;

    public SQLiteSteamStore(String dbURL){
        this.dbURL = dbURL;
        createTable();
    }
    @Override
    public void save(List<SteamGame> games) {
        String sql = "INSERT INTO steam_games(app_id, name, players, captured_at) VALUES(?,?,?,?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String capturedAt = LocalDateTime.now().toString();

            for (SteamGame game : games) {
                stmt.setString(1, game.getAppId());
                stmt.setString(2, game.getName());
                if (game.getCurrentPlayers().isPresent()) {
                    stmt.setInt(3, game.getCurrentPlayers().getAsInt());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }


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
        return DriverManager.getConnection(dbURL);
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS steam_games (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "app_id TEXT," +
                "name TEXT," +

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

}

 */