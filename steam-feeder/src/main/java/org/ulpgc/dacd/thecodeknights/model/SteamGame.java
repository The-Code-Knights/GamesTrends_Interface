package org.ulpgc.dacd.thecodeknights.model;

import java.util.OptionalInt;

public class SteamGame {
    private final String appId;
    private final String name;
    private final OptionalInt currentPlayers;

    public SteamGame(String appId, String name, OptionalInt currentPlayers) {
        if (appId == null || appId.isEmpty()) {
            throw new IllegalArgumentException("appId cannot be null or empty");
        }

        this.appId = appId;
        this.name = name;
        this.currentPlayers = currentPlayers;
    }

    public String getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public OptionalInt getCurrentPlayers() {
        return currentPlayers;
    }
}

