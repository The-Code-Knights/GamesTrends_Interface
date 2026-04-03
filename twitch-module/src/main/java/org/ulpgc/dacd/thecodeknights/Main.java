package org.ulpgc.dacd.thecodeknights;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        TwitchConfigProvider config = new TwitchConfigProvider();
        TwitchApiConsumer consumer = new TwitchApiConsumer(config);
        TwitchStreamAggregator aggregator = new TwitchStreamAggregator(consumer);

        List<Stream> streams = consumer.getSafeFetchStreams(10000);

        Map<String, GameStats> statsPerGame = aggregator.aggregateByGame(streams);

        List<String> gameIds = new ArrayList<>(statsPerGame.keySet());

        Map<String, String> gameNames = consumer.getGameNamesByIds(gameIds);

        for (Map.Entry<String, GameStats> entry : statsPerGame.entrySet()) {
            String gameId = entry.getKey();
            GameStats gs = entry.getValue();

            String name = gameNames.getOrDefault(gameId, "Unknown Game");
            gs.setGameName(name);
        }

        System.out.println("\n=== Estadísticas por Juego ===");
        System.out.println("Nº juegos distintos: " + statsPerGame.size());
        for (GameStats gs : statsPerGame.values()) {
            System.out.printf("Juego: %s, Total Viewers: %d, Total Streams: %d%n",
                    gs.getGameName(), gs.getTotalViewers(), gs.getTotalStreams());
        }

        System.out.println("Nº total de llamadas a la API: " + consumer.getApiCallCount());
    }
}