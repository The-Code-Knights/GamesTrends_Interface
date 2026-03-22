package org.ulpgc.dacd.thecodeknights;

import com.google.gson.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class TwitchApiClient{

    private static final String BASE_URL = "https://api.twitch.tv/helix";

    public static void main(String[] args) throws IOException {
        String token = ConfigLoader.getToken();
        String clientId = ConfigLoader.getClientId();
        String gameName = "League of Legends";

        String gameId = getGameId(gameName, token, clientId);
        int viewers = getViewerCount(gameId, token, clientId);

        System.out.println("Game: " + gameName);
        System.out.println("Viewers: " + viewers);
    }

    private static String getGameId(String gameName, String token, String clientId) throws IOException {
        String url = "/games?name=" + gameName.replace(" ", "%20");

        Connection.Response response = request(url, token, clientId);
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

        return json.getAsJsonArray("data")
                .get(0)
                .getAsJsonObject()
                .get("id")
                .getAsString();
    }

    private static int getViewerCount(String gameId, String token, String clientId) throws IOException {
        String url = "/streams?game_id=" + gameId;

        Connection.Response response = request(url, token, clientId);
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

        JsonArray streams = json.getAsJsonArray("data");

        System.out.println("Streams recibidos: " + streams.size());

        int totalViewers = 0;

        for (JsonElement element : streams) {
            JsonObject stream = element.getAsJsonObject();
            totalViewers += stream.get("viewer_count").getAsInt();
        }

        return totalViewers;
    }

    private static Connection.Response request(String path, String token, String clientId) throws IOException {
        return Jsoup.connect(BASE_URL + path)
                .ignoreContentType(true)
                .header("Authorization", "Bearer " + token)
                .header("Client-Id", clientId)
                .method(Connection.Method.GET)
                .execute();
    }
}
