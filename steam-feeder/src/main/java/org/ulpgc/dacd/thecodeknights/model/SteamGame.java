package org.ulpgc.dacd.thecodeknights.model;

public class SteamGame {
    private final String appId;
    private final String name;
    private final Integer currentPlayers;

    public SteamGame(String appId, String name, Integer currentPlayers) {
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

    public Integer getCurrentPlayers() {
        return currentPlayers;
    }
}
