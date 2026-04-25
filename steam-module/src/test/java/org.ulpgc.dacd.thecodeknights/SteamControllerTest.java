package org.ulpgc.dacd.thecodeknights;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.controller.SteamConsumer;
import org.ulpgc.dacd.thecodeknights.controller.SteamController;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SteamControllerTest {

    private SteamController controller;
    private ConsumerTest consumer;
    private StoreTest store;

    @BeforeEach
    void setUp() {
        consumer = new ConsumerTest();
        store = new StoreTest();
        controller = new SteamController(consumer, store);
    }

    @Test
    void shouldCallStoreWhenExecute() {
        // WHEN
        controller.execute();

        // THEN
        assertTrue(store.wasCalled);
    }

    @Test
    void shouldPassGamesToStore() {
        // WHEN
        controller.execute();

        // THEN
        assertNotNull(store.savedGames);
        assertEquals(1, store.savedGames.size());
        assertEquals("123", store.savedGames.get(0).getAppId());
    }
    @Test
    void shouldHandleEmptyGameList() {
        // GIVEN
        SteamConsumer consumer = () -> List.of(); // lista vacía
        StoreTest store = new StoreTest();

        SteamController controller = new SteamController(consumer, store);

        // WHEN
        controller.execute();

        // THEN
        assertTrue(store.wasCalled);
        assertTrue(store.savedGames.isEmpty());
    }
}
