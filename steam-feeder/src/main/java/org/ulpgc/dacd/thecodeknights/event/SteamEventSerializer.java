package org.ulpgc.dacd.thecodeknights.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SteamEventSerializer {

    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    public String serialize(SteamEvent event) {
        return gson.toJson(event);
    }
}
