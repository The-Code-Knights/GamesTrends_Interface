package org.ulpgc.dacd.thecodeknights.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SteamGameNamesConsumer {
    private final SteamHttpGestor httpClient;

    public SteamGameNamesConsumer(SteamHttpGestor httpClient) {
        this.httpClient = httpClient;
    }

    public String getGameName(String appId) {
        try {
            String url = "https://store.steampowered.com/api/appdetails?appids=" + appId;
            String json = httpClient.fetchJson(url);

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject appObj = root.getAsJsonObject(appId);

            if (appObj == null || !appObj.get("success").getAsBoolean()) {
                throw new IllegalArgumentException();
            }

            JsonObject data_key = appObj.getAsJsonObject("data");
            if (!data_key.get("type").getAsString().equals("game")) {
                return null;
            }

            return data_key.get("name").getAsString();

        } catch (Exception e) {
            System.err.println("Error obteniendo nombre para appId " + appId + ": " + e.getMessage());
            return null;
        }
    }

}
