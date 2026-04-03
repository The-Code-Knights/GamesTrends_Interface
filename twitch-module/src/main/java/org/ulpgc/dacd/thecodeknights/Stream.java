package org.ulpgc.dacd.thecodeknights;

public class Stream {

    private final String id;
    private final String userName;
    private final String gameId;
    private final String title;
    private final int viewerCount;

    public Stream(String id, String userName, String gameId, String title, int viewerCount) {
        this.id = id;
        this.userName = userName;
        this.gameId = gameId;
        this.title = title;
        this.viewerCount = viewerCount;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getGameId() {
        return gameId;
    }

    public String getTitle() {
        return title;
    }

    public int getViewerCount() {
        return viewerCount;
    }
}