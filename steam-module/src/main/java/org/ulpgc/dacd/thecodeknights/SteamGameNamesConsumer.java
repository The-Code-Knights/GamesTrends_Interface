package org.ulpgc.dacd.thecodeknights;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SteamGameNamesConsumer {

    public String getGameName(String appId) {
        try {
            String URL = "https://store.steampowered.com/api/appdetails?appids=" + appId; //No estaba
            Connection.Response response = Jsoup.connect(URL)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            String json = response.body();

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject appObj = root.getAsJsonObject(appId);

            if (appObj == null || !appObj.get("success").getAsBoolean()) {
                throw new IllegalArgumentException("Error ID not Found in URL name");
            }
            JsonObject data_key = appObj.getAsJsonObject("data");
            if (!data_key.get("type").getAsString().equals("game")){
                return null;
            }
            else {return data_key.get("name").getAsString();
            }


        } catch (Exception e) {
            //throw new RuntimeException("Error en la clase SteamGameNamesConsumer de tipo", e);
            System.err.println("Error obteniendo nombre con identificador: " + appId);
        }
        return null;

    }
}
