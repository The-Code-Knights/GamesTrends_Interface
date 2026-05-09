package org.ulpgc.dacd.thecodeknights.controller;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class SteamParser {

    public List<String> parseGamesTopId(String json) {
        List<String> gamesIds = new ArrayList<>();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonObject response = root.getAsJsonObject("response");
        JsonArray ranks = response.getAsJsonArray("ranks");

        for (JsonElement element : ranks) {
            JsonObject obj = element.getAsJsonObject();
            gamesIds.add(obj.get("appid").getAsString());
        }

        return gamesIds;
    }

    public String parseGameName(String json, String appId) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonObject appObj = root.getAsJsonObject(appId);

        if (appObj == null || !appObj.get("success").getAsBoolean()) {
            return null;
        }

        JsonObject data = appObj.getAsJsonObject("data");
        if (!"game".equals(data.get("type").getAsString())) {
            return null;
        }

        return data.get("name").getAsString();
    }

    public Integer parseCurrentPlayers(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonObject response = root.getAsJsonObject("response");

        if (response != null && response.has("player_count")) {
            return response.get("player_count").getAsInt();
        }

        return null;
    }
}
