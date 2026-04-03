package org.ulpgc.dacd.thecodeknights;


import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class SteamParser {

    public List<SteamGame> parseGames(String json) {
        List<SteamGame> games = new ArrayList<>();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonObject response = root.getAsJsonObject("response");
        JsonArray ranks = response.getAsJsonArray("ranks");

        for (JsonElement element : ranks) {
            JsonObject obj = element.getAsJsonObject();

            SteamGame game = new SteamGame();
            game.appId = obj.get("appid").getAsString();
            //game.Rank = obj.get("rank").getAsInt();
            games.add(game);
        }

        return games;
    }
}
