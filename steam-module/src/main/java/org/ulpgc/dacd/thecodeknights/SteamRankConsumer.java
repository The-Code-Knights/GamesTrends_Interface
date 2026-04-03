package org.ulpgc.dacd.thecodeknights;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.IOException;


public class SteamRankConsumer {
    private static final String URL = "https://api.steampowered.com/ISteamChartsService/GetMostPlayedGames/v1/";

    public String fetchTopGames() throws IOException {
        Connection.Response response = Jsoup.connect(URL)
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .execute();

        return response.body();
    }
}
