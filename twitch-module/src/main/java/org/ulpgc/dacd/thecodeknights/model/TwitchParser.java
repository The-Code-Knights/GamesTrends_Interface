package org.ulpgc.dacd.thecodeknights.model;

import com.google.gson.*;
import java.util.*;

public class TwitchParser {

    public List<Stream> parseStreams(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray data = jsonObject.getAsJsonArray("data");
        List<Stream> streams = new ArrayList<>();

        for (JsonElement element : data) {
            JsonObject streamJson = element.getAsJsonObject();
            streams.add(new Stream(
                    streamJson.get("id").getAsString(),
                    streamJson.get("user_name").getAsString(),
                    streamJson.get("game_id").getAsString(),
                    streamJson.get("title").getAsString(),
                    streamJson.get("viewer_count").getAsInt()
            ));
        }
        return streams;
    }

    public Map<String, String> parseGameNames(String jsonResponse) {
        JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray data = json.getAsJsonArray("data");

        Map<String, String> result = new HashMap<>();

        for (JsonElement element : data) {
            JsonObject game = element.getAsJsonObject();
            result.put(
                    game.get("id").getAsString(),
                    game.get("name").getAsString()
            );
        }
        return result;
    }

    public String extractCursor(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject pagination = jsonObject.getAsJsonObject("pagination");
        return pagination.has("cursor") ? pagination.get("cursor").getAsString() : null;
    }
}