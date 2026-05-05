package org.ulpgc.dacd.thecodeknights;
/*
import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.controller.SQLiteSteamStore;
import org.ulpgc.dacd.thecodeknights.model.SteamGame;

import java.sql.*;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

public class SQLiteSteamStoreTest {

    @Test
    void shouldInsertGameIntoDatabase() throws SQLException {
        // GIVEN
        String dbUrl = "jdbc:sqlite:test.db";
        SQLiteSteamStore store = new SQLiteSteamStore(dbUrl);

        SteamGame game = new SteamGame("999", "TestGame", OptionalInt.of(50));

        // WHEN
        store.save(List.of(game));

        // THEN
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM steam_games WHERE app_id = '999'"
             )) {

            assertTrue(rs.next());
            assertEquals("TestGame", rs.getString("name"));
            assertEquals(50, rs.getInt("players"));
        }
    }
}


 */