package org.ulpgc.dacd.thecodeknights;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchStreamAggregator {

    private final TwitchApiConsumer consumer;

    public TwitchStreamAggregator(TwitchApiConsumer consumer) {
        this.consumer = consumer;
    }

    public Map<String, GameStats> aggregateByGame(List<Stream> streams) {
        Map<String, GameStats> statsPerGame = new HashMap<>();

        for (Stream stream : streams) {
            String gameId = stream.getGameId();

            statsPerGame.compute(gameId, (id, stats) -> {
                if (stats == null) {
                    return new GameStats(stream.getViewerCount(), 1);
                } else {
                    stats.addStream(stream.getViewerCount());
                    return stats;
                }
            });
        }

        return statsPerGame;
    }
}