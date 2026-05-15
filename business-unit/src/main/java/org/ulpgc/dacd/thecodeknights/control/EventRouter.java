package org.ulpgc.dacd.thecodeknights.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.datamart.DatamartRepository;
import org.ulpgc.dacd.thecodeknights.control.event.SteamEvent;
import org.ulpgc.dacd.thecodeknights.control.event.TwitchEvent;

import java.util.Map;

public class EventRouter {

    private static final Logger logger = LoggerFactory.getLogger(EventRouter.class);

    private static final Map<String, String> NAME_ALIASES = Map.ofEntries(
        Map.entry("counter-strike 2",                   "Counter-Strike"),
        Map.entry("red dead redemption ii",              "Red Dead Redemption 2"),
        Map.entry("resident evil: requiem",              "Resident Evil Requiem"),
        Map.entry("resident evil requiem",               "Resident Evil Requiem"),
        Map.entry("slay the spire ii",                   "Slay the Spire 2"),
        Map.entry("conan exiles enhanced",               "Conan Exiles"),
        Map.entry("tom clancy’s rainbow six siege", "Rainbow Six Siege")
    );

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
            logger.error("Error enrutando evento: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private SteamEvent parseSteam(JsonObject obj) {
        JsonElement playersEl = obj.get("players");
        if (playersEl == null || playersEl.isJsonNull()) return null;
        return new SteamEvent(
                getString(obj, "ts"),
                getString(obj, "ss"),
                getString(obj, "appId"),
                normalizeName(getString(obj, "name")),
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
                normalizeName(getString(obj, "gameName")),
                getString(obj, "title"),
                getInt(obj, "viewerCount")
        );
    }

    static String normalizeName(String name) {
        if (name == null || name.isEmpty()) return name;
        String normalized = name
                .replace("™", "")
                .replace("®", "")
                .replace("©", "")
                .replace("’", "’")
                .replace("‘", "’")
                .replaceAll("[:\\-]\\s*$", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
        String alias = NAME_ALIASES.get(normalized.toLowerCase());
        return alias != null ? alias : normalized;
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
