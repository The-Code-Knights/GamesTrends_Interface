package org.ulpgc.dacd.thecodeknights.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.thecodeknights.datamart.DatamartRepository;
import org.ulpgc.dacd.thecodeknights.model.SteamEvent;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

public class EventRouter {

    private static final String STEAM_SOURCE  = "steam-feeder";
    private static final String TWITCH_SOURCE = "twitch-feeder";

    private final DatamartRepository datamart;

    public EventRouter(DatamartRepository datamart) {
        this.datamart = datamart;
    }

    public void route(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String ss = getString(obj, "ss");

            if (STEAM_SOURCE.equals(ss)) {
                SteamEvent event = parseSteam(obj);
                if (event != null) datamart.upsertSteam(event);
            } else if (TWITCH_SOURCE.equals(ss)) {
                datamart.upsertTwitch(parseTwitch(obj));
            }
        } catch (Exception e) {
            System.err.println("Error enrutando evento: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private SteamEvent parseSteam(JsonObject obj) {
        JsonElement playersEl = obj.get("players");
        if (playersEl == null || playersEl.isJsonNull()) return null;
        return new SteamEvent(
                getString(obj, "ts"),
                getString(obj, "ss"),
                getString(obj, "appId"),
                getString(obj, "name"),
                playersEl.getAsInt()
        );
    }

    private TwitchEvent parseTwitch(JsonObject obj) {
        return new TwitchEvent(
                getString(obj, "ts"),
                getString(obj, "ss"),
                getString(obj, "streamId"),
                getString(obj, "userName"),
                getString(obj, "gameId"),
                getString(obj, "gameName"),
                getString(obj, "title"),
                getInt(obj, "viewerCount")
        );
    }

    private String getString(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        return (el == null || el.isJsonNull()) ? "" : el.getAsString();
    }

    private int getInt(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        return (el == null || el.isJsonNull()) ? 0 : el.getAsInt();
    }
}
