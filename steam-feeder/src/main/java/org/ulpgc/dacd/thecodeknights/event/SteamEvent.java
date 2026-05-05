package org.ulpgc.dacd.thecodeknights.event;


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
    public Integer getPlayers() { return players; }
}
