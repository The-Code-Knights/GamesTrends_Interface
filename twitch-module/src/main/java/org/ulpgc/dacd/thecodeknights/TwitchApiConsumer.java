package org.ulpgc.dacd.thecodeknights;

import com.google.gson.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchApiConsumer {

    private final ConfigProviderInterface config;
    private final Gson gson;
    private int apiCallCount = 0; // contador

    public TwitchApiConsumer(ConfigProviderInterface config) {
        this.config = config;
        this.gson = new Gson();
    }

    public List<Stream> fetchStreams(int totalStreams) throws IOException {
        List<Stream> allStreams = new ArrayList<>();
        String cursor = null;
        int remaining = totalStreams;
        while (remaining > 0) {
            int fetchCount = Math.min(100, remaining);
            String url = "https://api.twitch.tv/helix/streams?first=" + fetchCount;
            if (cursor != null) {
                url += "&after=" + cursor;
            }
            String jsonResponse = callApi(url);
            List<Stream> streams = parseStreams(jsonResponse);
            allStreams.addAll(streams);
            remaining -= streams.size();
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject pagination = jsonObject.getAsJsonObject("pagination");
            cursor = pagination.has("cursor") ? pagination.get("cursor").getAsString() : null;
            if (cursor == null || streams.isEmpty()) {
                break;
            }
        }
        return allStreams;
    }

    private Map<String, String> fetchGameNamesBatch(List<String> gameIds) throws IOException {

        StringBuilder url = new StringBuilder("https://api.twitch.tv/helix/games?");
        for (String id : gameIds) {
            url.append("id=").append(id).append("&");
        }
        String jsonResponse = callApi(url.toString());
        return parseGameNames(jsonResponse);
    }

    public Map<String, String> getGameNamesByIds(List<String> gameIds) {
        Map<String, String> allGameNames = new HashMap<>();
        int batchSize = 100;
        for (int i = 0; i < gameIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, gameIds.size());
            List<String> batch = gameIds.subList(i, end);
            try {
                Map<String, String> batchResult = fetchGameNamesBatch(batch);
                allGameNames.putAll(batchResult);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        return allGameNames;
    }

    private String callApi(String url) throws IOException {
        apiCallCount++;
        Connection.Response response = Jsoup.connect(url)
                .ignoreContentType(true)
                .header("Client-ID", config.getClientId())
                .header("Authorization", "Bearer " + config.getToken())
                .method(Connection.Method.GET)
                .execute();
        return response.body();
    }

    private List<Stream> parseStreams(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray data = jsonObject.getAsJsonArray("data");
        List<Stream> streams = new ArrayList<>();

        for (JsonElement element : data) {
            JsonObject streamJson = element.getAsJsonObject();
            Stream stream = new Stream(
                    streamJson.get("id").getAsString(),
                    streamJson.get("user_name").getAsString(),
                    streamJson.get("game_id").getAsString(),
                    streamJson.get("title").getAsString(),
                    streamJson.get("viewer_count").getAsInt()
            );
            streams.add(stream);
        }
        return streams;
    }

    private Map<String, String> parseGameNames(String jsonResponse) {
        JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray data = json.getAsJsonArray("data");

        Map<String, String> result = new HashMap<>();

        for (JsonElement element : data) {
            JsonObject game = element.getAsJsonObject();
            String id = game.get("id").getAsString();
            String name = game.get("name").getAsString();
            result.put(id, name);
        }

        return result;
    }

    public List<Stream> getSafeFetchStreams(int first) {
        try {
            return fetchStreams(first);
        } catch (IOException error) {
            error.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int getApiCallCount() {
        return apiCallCount;
    }

}