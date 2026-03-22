package org.ulpgc.dacd.thecodeknights;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SteamCurrentPlayer {

    private final String appId;

    public SteamCurrentPlayer(String appId) {
        this.appId = appId;
    }

    private String buildUrl() {
        return "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=" + appId;
    }

    public int getCurrentPlayers() {
        try {
            Connection.Response response = Jsoup.connect(buildUrl())
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            String json = response.body();

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject responseObj = root.getAsJsonObject("response");

            return responseObj.get("player_count").getAsInt();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error obteniendo jugadores actuales para appId: " + appId, e
            );
        }
    }
}