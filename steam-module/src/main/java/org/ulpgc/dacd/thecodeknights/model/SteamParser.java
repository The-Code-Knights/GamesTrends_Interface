package org.ulpgc.dacd.thecodeknights.model;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class SteamParser {

    public List<String> parseGamesTopId(String json) {
        List<String> GamesIds = new ArrayList<>();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonObject response = root.getAsJsonObject("response");
        JsonArray ranks = response.getAsJsonArray("ranks");

        for (JsonElement element : ranks) {
            JsonObject obj = element.getAsJsonObject();
            GamesIds.add(obj.get("appid").getAsString());
        }

        return GamesIds;
    }
}
