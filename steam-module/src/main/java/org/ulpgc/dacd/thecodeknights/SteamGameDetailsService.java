package org.ulpgc.dacd.thecodeknights;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SteamGameDetailsService {

    private final String appId;

    // ✔ Constructor correcto
    public SteamGameDetailsService(String appId) {
        this.appId = appId;
    }

    // ✔ Construir URL
    private String buildUrl() {
        return "https://store.steampowered.com/api/appdetails?appids=" + appId;
    }

    // ✔ Llamada HTTP + parseo del nombre
    public String getGameName() {
        try {
            Connection.Response response = Jsoup.connect(buildUrl())
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            String json = response.body();

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject appObj = root.getAsJsonObject(appId);

            if (appObj == null || !appObj.get("success").getAsBoolean()) {
                return "UNKNOWN";
            }

            return appObj.getAsJsonObject("data")
                    .get("name")
                    .getAsString();

        } catch (Exception e) {
            throw new RuntimeException("Error en la clase SteamGmeDetailsService de tipo", e);
        }
    }
}
