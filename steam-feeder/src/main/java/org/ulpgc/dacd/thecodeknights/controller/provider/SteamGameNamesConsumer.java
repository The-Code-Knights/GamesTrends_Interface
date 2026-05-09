package org.ulpgc.dacd.thecodeknights.controller.provider;


import org.ulpgc.dacd.thecodeknights.controller.SteamParser;

public class SteamGameNamesConsumer {

    private final SteamHttpGestor httpClient;
    private final SteamParser parser;

    public SteamGameNamesConsumer(SteamHttpGestor httpClient, SteamParser parser) {
        this.httpClient = httpClient;
        this.parser = parser;
    }

    public String getGameName(String appId) {
        try {
            String url = "https://store.steampowered.com/api/appdetails?appids=" + appId;
            String json = httpClient.fetchJson(url);

            return parser.parseGameName(json, appId);

        } catch (Exception e) {
            System.err.println("Error obteniendo nombre para appId " + appId + ": " + e.getMessage());
            return null;
        }
    }
}
