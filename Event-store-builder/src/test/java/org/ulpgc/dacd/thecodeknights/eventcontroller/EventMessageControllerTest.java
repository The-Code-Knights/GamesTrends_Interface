package org.ulpgc.dacd.thecodeknights.eventcontroller;

import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.eventstore.EventStore;
import org.ulpgc.dacd.thecodeknights.eventstore.EventStoreException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventMessageControllerTest {

    @Test
    void handle_validEvent_callsSave() {
        List<String> saved = new ArrayList<>();
        EventStore fakeStore = (topic, json) -> saved.add(topic + ":" + json);

        EventMessageController controller = new EventMessageController(fakeStore);
        controller.handle("steam.games", "{\"ts\":\"2024-01-01\"}");

        assertEquals(1, saved.size());
        assertTrue(saved.get(0).startsWith("steam.games:"));
    }

    @Test
    void handle_storeThrowsException_doesNotPropagate() {
        EventStore fakeStore = (topic, json) -> { throw new EventStoreException("fail"); };

        EventMessageController controller = new EventMessageController(fakeStore);
        assertDoesNotThrow(() -> controller.handle("steam.games", "{}"));
    }

    @Test
    void handle_multipleCalls_savesEach() {
        List<String> saved = new ArrayList<>();
        EventStore fakeStore = (topic, json) -> saved.add(json);

        EventMessageController controller = new EventMessageController(fakeStore);
        controller.handle("steam.games", "{\"ts\":\"2024-01-01\"}");
        controller.handle("twitch.streams", "{\"ts\":\"2024-01-02\"}");

        assertEquals(2, saved.size());
    }
}
