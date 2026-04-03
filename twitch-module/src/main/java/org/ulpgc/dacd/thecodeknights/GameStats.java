package org.ulpgc.dacd.thecodeknights;

public class GameStats {

    private String gameName;
    private int totalViewers;
    private int totalStreams;

    public GameStats(int viewers, int streams) {
        this.totalViewers = viewers;
        this.totalStreams = streams;
    }

    public String getGameName() {
        return gameName;
    }

    public int getTotalViewers() {
        return totalViewers;
    }

    public int getTotalStreams() {
        return totalStreams;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void addStream(int viewers) {
        this.totalStreams++;
        this.totalViewers += viewers;
    }
}