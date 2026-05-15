package org.ulpgc.dacd.thecodeknights;

import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.controller.SteamController;
import org.ulpgc.dacd.thecodeknights.controller.store.SteamEventPublisher;
import org.ulpgc.dacd.thecodeknights.model.SteamEvent;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SteamControllerTest {

    @Test
    void execute_withEvents_publishesAll() {
        SteamEvent event = new SteamEvent("2024-01-01T00:00:00Z", "steam-feeder", "730", "Counter-Strike 2", 1000000);

        List<SteamEvent> published = new ArrayList<>();
        SteamEventPublisher fakePublisher = new SteamEventPublisher("fake", "fake") {
            @Override
            public void publish(List<SteamEvent> events) {
                published.addAll(events);
            }
        };

        SteamController controller = new SteamController(() -> List.of(event), fakePublisher);
        controller.execute();

        assertEquals(1, published.size());
        assertEquals("730", published.get(0).getAppId());
    }

    @Test
    void execute_emptyList_publishesNothing() {
        List<SteamEvent> published = new ArrayList<>();
        SteamEventPublisher fakePublisher = new SteamEventPublisher("fake", "fake") {
            @Override
            public void publish(List<SteamEvent> events) {
                published.addAll(events);
            }
        };

        SteamController controller = new SteamController(List::of, fakePublisher);
        controller.execute();

        assertTrue(published.isEmpty());
    }
}
