package org.ulpgc.dacd.thecodeknights.controller.provider;

public class SteamRankConsumer {

    private static final String URL = "https://api.steampowered.com/ISteamChartsService/GetMostPlayedGames/v1/";
    private final SteamHttpGestor httpClient;

    public SteamRankConsumer(SteamHttpGestor httpClient) {
        this.httpClient = httpClient;
    }

    public String fetchTopGames() throws Exception {
        return httpClient.fetchJson(URL);
    }
}
