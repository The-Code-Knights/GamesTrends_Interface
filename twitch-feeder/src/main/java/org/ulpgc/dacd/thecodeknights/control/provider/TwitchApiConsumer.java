package org.ulpgc.dacd.thecodeknights.control.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.control.TwitchParser;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TwitchApiConsumer implements TwitchConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TwitchApiConsumer.class);

    private final String token;
    private final String clientId;
    private final TwitchParser parser;
    private final GameNameCache gameNameCache;
    private int apiCallCount = 0;

    public TwitchApiConsumer(String token, String clientId, TwitchParser parser, GameNameCache gameNameCache) {
        this.token = token;
        this.clientId = clientId;
        this.parser = parser;
        this.gameNameCache = gameNameCache;
    }

    @Override
    public List<TwitchEvent> fetchEvents(int totalEvents) throws IOException {
        apiCallCount = 0;
        List<TwitchEvent> allEvents = new ArrayList<>();
        String cursor = null;
        int remaining = totalEvents;

        while (remaining > 0) {
            int fetchCount = Math.min(100, remaining);
            String url = "https://api.twitch.tv/helix/streams?first=" + fetchCount;
            if (cursor != null) url += "&after=" + cursor;

            String jsonResponse = callApi(url);
            List<TwitchEvent> events = parser.parseEvents(jsonResponse);
            allEvents.addAll(events);

            remaining -= events.size();
            cursor = parser.extractCursor(jsonResponse);
            if (cursor == null || events.isEmpty()) break;
        }

        List<TwitchEvent> enrichedEvents = fillGameNames(allEvents);

        logger.info("Ejecución completada con éxito.");
        logger.info("Llamadas realizadas a la API de Twitch: {}", apiCallCount);

        return enrichedEvents;
    }

    private List<TwitchEvent> fillGameNames(List<TwitchEvent> events) throws IOException {
        Set<String> allGameIds = events.stream()
                .map(TwitchEvent::getGameId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toSet());

        Map<String, String> knownNames = gameNameCache.getKnownNames(allGameIds);

        Set<String> unknownIds = allGameIds.stream()
                .filter(id -> !knownNames.containsKey(id))
                .collect(Collectors.toSet());

        if (!unknownIds.isEmpty()) {
            Map<String, String> fetchedNames = fetchGameNames(new ArrayList<>(unknownIds));
            gameNameCache.saveNames(fetchedNames);
            knownNames.putAll(fetchedNames);
        }

        return events.stream()
                .map(e -> new TwitchEvent(
                        e.getTs(), e.getSs(), e.getStreamId(), e.getUserName(),
                        e.getGameId(), knownNames.getOrDefault(e.getGameId(), ""),
                        e.getTitle(), e.getViewerCount()))
                .collect(Collectors.toList());
    }

    private Map<String, String> fetchGameNames(List<String> gameIds) throws IOException {
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < gameIds.size(); i += 100) {
            List<String> batch = gameIds.subList(i, Math.min(i + 100, gameIds.size()));
            String url = "https://api.twitch.tv/helix/games?" +
                    batch.stream().map(id -> "id=" + id).collect(Collectors.joining("&"));
            parseGameNames(callApi(url), result);
        }
        return result;
    }

    private void parseGameNames(String json, Map<String, String> result) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        JsonArray data = obj.getAsJsonArray("data");
        for (JsonElement el : data) {
            JsonObject game = el.getAsJsonObject();
            result.put(game.get("id").getAsString(), game.get("name").getAsString());
        }
    }

    private String callApi(String url) throws IOException {
        apiCallCount++;
        Connection.Response response = Jsoup.connect(url)
                .ignoreContentType(true)
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + token)
                .method(Connection.Method.GET)
                .execute();
        return response.body();
    }

    @Override
    public int getApiCallCount() {
        return apiCallCount;
    }
}
