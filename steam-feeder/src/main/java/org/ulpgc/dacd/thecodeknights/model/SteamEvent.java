package org.ulpgc.dacd.thecodeknights.model;


public class SteamEvent {

    private final String ts;
    private final String ss;
    private final String appId;
    private final String name;
    private final Integer players;

    public SteamEvent(String ts, String ss, String appId, String name, Integer players) {
        if (ts == null || ts.isBlank()) {
            throw new IllegalArgumentException("ts cannot be null or blank");
        }

        if (ss == null || ss.isBlank()) {
            throw new IllegalArgumentException("ss cannot be null or blank");
        }

        if (appId == null || appId.isBlank()) {
            throw new IllegalArgumentException("appId cannot be null or blank");
        }

        this.ts = ts;
        this.ss = ss;
        this.appId = appId;
        this.name = name;
        this.players = players;
    }

    public String getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public String getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public Integer getPlayers() {
        return players;
    }

    public boolean hasPlayers() {
        return players != null;
    }
}


/*
public class SteamEvent {
    private final String ts;
    private final String ss;

    private final String appId;
    private final String name;
    private final Integer players;

    public SteamEvent(String ts, String ss, String appId, String name, Integer players) {
        this.ts = ts;
        this.ss = ss;
        this.appId = appId;
        this.name = name;
        this.players = players;
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public String getAppId() { return appId; }
    public String getName() { return name; }
    public Integer getCurrentPlayers() { return players; }
}


 */