package org.ulpgc.dacd.thecodeknights.datamart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.ulpgc.dacd.thecodeknights.control.event.SteamEvent;
import org.ulpgc.dacd.thecodeknights.control.event.TwitchEvent;
import org.ulpgc.dacd.thecodeknights.model.GameAnalytics;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatamartRepositoryTest {

    @TempDir
    Path tempDir;

    private DatamartRepository repo;

    @BeforeEach
    void setUp() {
        repo = new DatamartRepository(tempDir.resolve("test.db").toString());
    }

    @Test
    void upsertSteam_insertsRecord_appearsInAnalytics() {
        SteamEvent event = new SteamEvent("2024-01-01T00:00:00Z", "steam-feeder", "730", "Counter-Strike", 500000);
        repo.upsertSteam(event);

        List<GameAnalytics> analytics = repo.getAllAnalytics();
        assertEquals(1, analytics.size());
        assertEquals("Counter-Strike", analytics.get(0).getGameName());
        assertEquals(500000, analytics.get(0).getSteamPlayers());
    }

    @Test
    void upsertSteam_updatesExisting_playerCountUpdated() {
        SteamEvent first  = new SteamEvent("2024-01-01T00:00:00Z", "steam-feeder", "730", "Counter-Strike", 500000);
        SteamEvent second = new SteamEvent("2024-01-02T00:00:00Z", "steam-feeder", "730", "Counter-Strike", 600000);

        repo.upsertSteam(first);
        repo.upsertSteam(second);

        List<GameAnalytics> analytics = repo.getAllAnalytics();
        assertEquals(1, analytics.size());
        assertEquals(600000, analytics.get(0).getSteamPlayers());
    }

    @Test
    void upsertTwitch_insertsStream_viewersJoinWithSteam() {
        SteamEvent steam = new SteamEvent("2024-01-01T00:00:00Z", "steam-feeder", "730", "Counter-Strike", 500000);
        TwitchEvent twitch = new TwitchEvent("2024-01-01T00:00:00Z", "twitch-feeder", "s1", "streamer1", "g1", "Counter-Strike", "Playing!", 10000);

        repo.upsertSteam(steam);
        repo.upsertTwitch(twitch);

        List<GameAnalytics> analytics = repo.getAllAnalytics();
        GameAnalytics game = analytics.stream()
            .filter(g -> g.getGameName().equals("Counter-Strike"))
            .findFirst().orElseThrow();

        assertEquals(10000, game.getTwitchViewers());
        assertEquals(1, game.getTwitchStreams());
        assertTrue(game.getStreamPotentialRatio() > 0);
    }

    @Test
    void getAllAnalytics_noData_returnsEmptyList() {
        assertTrue(repo.getAllAnalytics().isEmpty());
    }
}
