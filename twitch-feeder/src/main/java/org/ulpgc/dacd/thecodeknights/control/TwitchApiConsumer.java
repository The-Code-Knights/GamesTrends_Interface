package org.ulpgc.dacd.thecodeknights.control;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.ulpgc.dacd.thecodeknights.model.Stream;
import org.ulpgc.dacd.thecodeknights.model.TwitchParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchApiConsumer implements TwitchConsumer {

    private final String token;
    private final String clientId;
    private final TwitchParser parser;
    private int apiCallCount = 0;

    public TwitchApiConsumer(String token, String clientId, TwitchParser parser) {
            this.token = token;
            this.clientId = clientId;
            this.parser = parser;
    }

    @Override
    public List<Stream> fetchStreams(int totalStreams) throws IOException {
        apiCallCount = 0;
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
            List<Stream> streams = parser.parseStreams(jsonResponse);
            allStreams.addAll(streams);
            remaining -= streams.size();
            cursor = parser.extractCursor(jsonResponse);
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
        return parser.parseGameNames(jsonResponse);
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
                System.err.println("Error fetching game names: " + error.getMessage());
            }
        }
        return allGameNames;
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