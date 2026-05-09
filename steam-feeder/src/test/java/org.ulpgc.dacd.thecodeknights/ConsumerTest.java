/*
package org.ulpgc.dacd.thecodeknights;

import org.ulpgc.dacd.thecodeknights.controller.provider.SteamConsumer;
import org.ulpgc.dacd.thecodeknights.model.SteamGame;
import org.ulpgc.dacd.thecodeknights.controller.SteamStore;

import java.util.List;
import java.util.OptionalInt;

class ConsumerTest implements SteamConsumer {
    @Override
    public List<SteamGame> getGames() {
        SteamGame game = new SteamGame("123", "TestGame", OptionalInt.of(100));
        return List.of(game);
    }
}

class StoreTest implements SteamStore {
    boolean wasCalled = false;
    List<SteamGame> savedGames;

    @Override
    public void save(List<SteamGame> games) {
        wasCalled = true;
        savedGames = games;
    }
}


 */