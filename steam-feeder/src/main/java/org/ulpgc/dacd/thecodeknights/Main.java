package org.ulpgc.dacd.thecodeknights;
import org.ulpgc.dacd.thecodeknights.controller.*;
import org.ulpgc.dacd.thecodeknights.model.SteamParser;
import org.ulpgc.dacd.thecodeknights.controller.SteamStore;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        String dbURL = args[0];

        SteamHttpGestor httpClient = new SteamHttpGestor();

        SteamRankConsumer rankConsumer = new SteamRankConsumer(httpClient);
        SteamParser parser = new SteamParser();

        SteamGameNamesConsumer nameConsumer = new SteamGameNamesConsumer(httpClient, parser);
        SteamCurrentPlayerConsumer playerConsumer = new SteamCurrentPlayerConsumer(httpClient, parser);

        SteamConsumer consumer = new SteamApiConsumer(rankConsumer, parser, nameConsumer, playerConsumer);

        SteamStore serializer = new SQLiteSteamStore(dbURL);

        SteamController controller = new SteamController(consumer, serializer);

        controller.start(0, 8, TimeUnit.HOURS);
    }
}
