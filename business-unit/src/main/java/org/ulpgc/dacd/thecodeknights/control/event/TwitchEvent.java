package org.ulpgc.dacd.thecodeknights.control.event;

public class TwitchEvent {

    private final String ts;
    private final String ss;
    private final String streamId;
    private final String userName;
    private final String gameId;
    private final String gameName;
    private final String title;
    private final int viewerCount;

    public TwitchEvent(String ts, String ss, String streamId, String userName,
                       String gameId, String gameName, String title, int viewerCount) {
        this.ts = ts;
        this.ss = ss;
        this.streamId = streamId;
        this.userName = userName;
        this.gameId = gameId;
        this.gameName = gameName;
        this.title = title;
        this.viewerCount = viewerCount;
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public String getStreamId() { return streamId; }
    public String getUserName() { return userName; }
    public String getGameId() { return gameId; }
    public String getGameName() { return gameName; }
    public String getTitle() { return title; }
    public int getViewerCount() { return viewerCount; }
}
