package org.ulpgc.dacd.thecodeknights.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.OptionalInt;

public class SteamCurrentPlayerConsumer {
    private final SteamHttpGestor httpClient;

    public SteamCurrentPlayerConsumer(SteamHttpGestor httpClient) {
        this.httpClient = httpClient;
    }

    public OptionalInt getCurrentPlayers(String appId) {
        try {
            String url = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=" + appId;
            String json = httpClient.fetchJson(url);

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject responseObj = root.getAsJsonObject("response");

            if (responseObj != null && responseObj.has("player_count")) {
                return OptionalInt.of(responseObj.get("player_count").getAsInt());
            }

        } catch (Exception e) {
            System.err.println("Error obteniendo jugadores para appId " + appId + ": " + e.getMessage());
        }

        return OptionalInt.empty();
    }
}