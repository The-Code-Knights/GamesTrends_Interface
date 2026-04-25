import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.control.SQLiteTwitchStore;
import org.ulpgc.dacd.thecodeknights.model.Stream;
import org.ulpgc.dacd.thecodeknights.control.TwitchStore;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwitchStoreTest {

    private static final String DB_URL = "jdbc:sqlite:test_twitch.db";

    @Test
    void save_shouldPersistStreamsInDatabase() throws Exception {

        TwitchStore store = new SQLiteTwitchStore(DB_URL);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM twitch_streams");
        }

        List<Stream> streams = List.of(
                new Stream("1", "user1", "game1", "title1", 100),
                new Stream("2", "user2", "game2", "title2", 200)
        );

        store.save(streams);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM twitch_streams")) {

            assertTrue(rs.next());
            assertEquals(2, rs.getInt("total"));
        }
    }
}