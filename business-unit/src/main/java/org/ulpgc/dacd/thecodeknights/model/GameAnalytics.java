package org.ulpgc.dacd.thecodeknights.model;

public class GameAnalytics {

    private final String gameName;
    private final String appId;
    private final int steamPlayers;
    private final long twitchViewers;
    private final int twitchStreams;
    private final double streamPotentialRatio;
    private final double viewerPerStream;

    public GameAnalytics(String gameName, String appId, int steamPlayers, long twitchViewers,
                         int twitchStreams, double streamPotentialRatio, double viewerPerStream) {
        this.gameName = gameName;
        this.appId = appId;
        this.steamPlayers = steamPlayers;
        this.twitchViewers = twitchViewers;
        this.twitchStreams = twitchStreams;
        this.streamPotentialRatio = streamPotentialRatio;
        this.viewerPerStream = viewerPerStream;
    }

    public String getGameName() { return gameName; }
    public String getAppId() { return appId; }
    public int getSteamPlayers() { return steamPlayers; }
    public long getTwitchViewers() { return twitchViewers; }
    public int getTwitchStreams() { return twitchStreams; }
    public double getStreamPotentialRatio() { return streamPotentialRatio; }
    public double getViewerPerStream() { return viewerPerStream; }
}
