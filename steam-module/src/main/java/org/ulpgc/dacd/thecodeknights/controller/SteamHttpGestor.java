package org.ulpgc.dacd.thecodeknights.controller;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SteamHttpGestor {

    public String fetchJson(String url) throws Exception {
        Connection.Response response = Jsoup.connect(url)
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .execute();

        return response.body();
    }
}
