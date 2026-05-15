package org.ulpgc.dacd.thecodeknights.datamart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.model.GameAnalytics;
import org.ulpgc.dacd.thecodeknights.control.event.SteamEvent;
import org.ulpgc.dacd.thecodeknights.control.event.TwitchEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatamartRepository {

    private static final Logger logger = LoggerFactory.getLogger(DatamartRepository.class);

    private final String dbPath;
    private Connection batchConnection = null;

    public DatamartRepository(String dbPath) {
        this.dbPath = dbPath;
        initialize();
    }

    public void beginBatch() {
        try {
            batchConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            batchConnection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("Error iniciando batch: {}", e.getMessage());
        }
    }

    public void endBatch() {
        if (batchConnection == null) return;
        try {
            batchConnection.commit();
            batchConnection.close();
        } catch (SQLException e) {
            logger.error("Error finalizando batch: {}", e.getMessage());
        } finally {
            batchConnection = null;
        }
    }

    private Connection connect() throws SQLException {
        if (batchConnection != null) return batchConnection;
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS steam_snapshot (
                    app_id   TEXT PRIMARY KEY,
                    name     TEXT NOT NULL,
                    players  INTEGER NOT NULL,
                    updated_at TEXT NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS twitch_stream (
                    stream_id    TEXT PRIMARY KEY,
                    user_name    TEXT NOT NULL,
                    game_name    TEXT NOT NULL,
                    title        TEXT,
                    viewer_count INTEGER NOT NULL,
                    updated_at   TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            logger.error("Error inicializando datamart: {}", e.getMessage());
        }
    }

    public void upsertSteam(SteamEvent event) {
        String sql = """
            INSERT INTO steam_snapshot (app_id, name, players, updated_at)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(app_id) DO UPDATE SET
                name       = excluded.name,
                players    = excluded.players,
                updated_at = excluded.updated_at
        """;
        boolean ownConn = (batchConnection == null);
        try {
            Connection conn = connect();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, event.getAppId());
                ps.setString(2, event.getName());
                ps.setInt(3, event.getPlayers());
                ps.setString(4, event.getTs());
                ps.executeUpdate();
            } finally {
                if (ownConn) conn.close();
            }
        } catch (SQLException e) {
            logger.error("Error upsert steam: {}", e.getMessage());
        }
    }

    public void upsertTwitch(TwitchEvent event) {
        String sql = """
            INSERT INTO twitch_stream (stream_id, user_name, game_name, title, viewer_count, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT(stream_id) DO UPDATE SET
                user_name    = excluded.user_name,
                game_name    = excluded.game_name,
                title        = excluded.title,
                viewer_count = excluded.viewer_count,
                updated_at   = excluded.updated_at
        """;
        boolean ownConn = (batchConnection == null);
        try {
            Connection conn = connect();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, event.getStreamId());
                ps.setString(2, event.getUserName());
                ps.setString(3, event.getGameName());
                ps.setString(4, event.getTitle());
                ps.setInt(5, event.getViewerCount());
                ps.setString(6, event.getTs());
                ps.executeUpdate();
            } finally {
                if (ownConn) conn.close();
            }
        } catch (SQLException e) {
            logger.error("Error upsert twitch: {}", e.getMessage());
        }
    }

    public List<GameAnalytics> getRecommendations(int limit) {
        return queryAnalytics(limit);
    }

    public List<GameAnalytics> getAllAnalytics() {
        return queryAnalytics(Integer.MAX_VALUE);
    }

    private List<GameAnalytics> queryAnalytics(int limit) {

        String sql = """
            SELECT
                s.name       AS game_name,
                s.app_id,
                s.players    AS steam_players,
                COALESCE(SUM(t.viewer_count), 0)  AS twitch_viewers,
                COALESCE(COUNT(t.stream_id),  0)  AS twitch_streams,
                CASE WHEN s.players > 0
                     THEN CAST(COALESCE(SUM(t.viewer_count), 0) AS REAL) / s.players
                     ELSE 0.0 END AS stream_potential_ratio,
                CASE WHEN COUNT(t.stream_id) > 0
                     THEN CAST(SUM(t.viewer_count) AS REAL) / COUNT(t.stream_id)
                     ELSE 0.0 END AS viewer_per_stream
            FROM steam_snapshot s
            LEFT JOIN twitch_stream t ON LOWER(s.name) = LOWER(t.game_name)
            GROUP BY s.app_id, s.name, s.players
            ORDER BY stream_potential_ratio DESC
            LIMIT ?
        """;
        List<GameAnalytics> results = new ArrayList<>();
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new GameAnalytics(
                        rs.getString("game_name"),
                        rs.getString("app_id"),
                        rs.getInt("steam_players"),
                        rs.getLong("twitch_viewers"),
                        rs.getInt("twitch_streams"),
                        rs.getDouble("stream_potential_ratio"),
                        rs.getDouble("viewer_per_stream")
                ));
            }
        } catch (SQLException e) {
            logger.error("Error consultando analytics: {}", e.getMessage());
        }
        return results;
    }
}
