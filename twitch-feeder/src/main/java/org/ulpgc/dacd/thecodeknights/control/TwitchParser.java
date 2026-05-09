package org.ulpgc.dacd.thecodeknights.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TwitchParser {

    public List<TwitchEvent> parseEvents(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray data = jsonObject.getAsJsonArray("data");
        List<TwitchEvent> events = new ArrayList<>();

        String ts = Instant.now().toString();
        String ss = "twitch-feeder";

        for (JsonElement element : data) {
            JsonObject streamJson = element.getAsJsonObject();
            events.add(new TwitchEvent(
                    ts,
                    ss,
                    streamJson.get("id").getAsString(),
                    streamJson.get("user_name").getAsString(),
                    streamJson.get("game_id").getAsString(),
                    streamJson.get("title").getAsString(),
                    streamJson.get("viewer_count").getAsInt()
            ));
        }

        return events;
    }

    public String extractCursor(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject pagination = jsonObject.getAsJsonObject("pagination");
        return pagination.has("cursor") ? pagination.get("cursor").getAsString() : null;
    }
}
