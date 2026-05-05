package org.ulpgc.dacd.thecodeknights.controller;


import org.ulpgc.dacd.thecodeknights.model.SteamParser;

public class SteamCurrentPlayerConsumer {

    private final SteamHttpGestor httpClient;
    private final SteamParser parser;

    public SteamCurrentPlayerConsumer(SteamHttpGestor httpClient, SteamParser parser) {
        this.httpClient = httpClient;
        this.parser = parser;
    }

    public Integer getCurrentPlayers(String appId) {
        try {
            String url = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=" + appId;
            String json = httpClient.fetchJson(url);

            return parser.parseCurrentPlayers(json);

        } catch (Exception e) {
            System.err.println("Error obteniendo jugadores para appId " + appId + ": " + e.getMessage());
            return null;
        }
    }
}
