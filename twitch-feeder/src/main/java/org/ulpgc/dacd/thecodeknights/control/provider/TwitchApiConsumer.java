package org.ulpgc.dacd.thecodeknights.control.provider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.ulpgc.dacd.thecodeknights.control.TwitchParser;
import org.ulpgc.dacd.thecodeknights.control.provider.TwitchConsumer;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public List<TwitchEvent> fetchEvents(int totalEvents) throws IOException {
        apiCallCount = 0;
        List<TwitchEvent> allEvents = new ArrayList<>();
        String cursor = null;
        int remaining = totalEvents;

        while (remaining > 0) {
            int fetchCount = Math.min(100, remaining);
            String url = "https://api.twitch.tv/helix/streams?first=" + fetchCount;
            if (cursor != null) {
                url += "&after=" + cursor;
            }

            String jsonResponse = callApi(url);
            List<TwitchEvent> events = parser.parseEvents(jsonResponse);
            allEvents.addAll(events);

            remaining -= events.size();
            cursor = parser.extractCursor(jsonResponse);

            if (cursor == null || events.isEmpty()) {
                break;
            }
        }

        System.out.println("Ejecución completada con éxito.");
        System.out.println("Llamadas realizadas a la API de Twitch: " + apiCallCount);

        return allEvents;
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
