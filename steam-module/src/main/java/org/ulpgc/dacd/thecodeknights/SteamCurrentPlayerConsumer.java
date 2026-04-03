package org.ulpgc.dacd.thecodeknights;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SteamCurrentPlayerConsumer {

    public int getCurrentPlayers(String appId) {
        try {
            String URL = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=" + appId;
            Connection.Response response = Jsoup.connect(URL)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            String json = response.body();

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject responseObj = root.getAsJsonObject("response");
            if (responseObj.has("player_count")){
            return responseObj.get("player_count").getAsInt();
            }

        } catch (Exception e) {
            System.err.println("Error obteniendo jugadores actuales para appId: " + appId);

        }
        return 0;
    }
}