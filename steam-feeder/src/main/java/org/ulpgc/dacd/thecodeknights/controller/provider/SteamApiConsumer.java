package org.ulpgc.dacd.thecodeknights.controller.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.controller.SteamParser;
import org.ulpgc.dacd.thecodeknights.model.SteamEvent;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import java.time.Instant;


public class SteamApiConsumer implements SteamConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SteamApiConsumer.class);

    private static final String SOURCE = "steam-feeder";

    private final SteamRankConsumer rankConsumer;
    private final SteamParser parser;
    private final SteamGameNamesConsumer nameConsumer;
    private final SteamCurrentPlayerConsumer playerConsumer;

    private static final Set<String> BLACKLIST = Set.of(
            "431960", "480", "1366800", "1905180", "218",
            "1325860", "365670", "4333400", "1812620"
    );

    public SteamApiConsumer(SteamRankConsumer rankConsumer, SteamParser parser, SteamGameNamesConsumer nameConsumer,
                            SteamCurrentPlayerConsumer playerConsumer
    ) {
        this.rankConsumer = rankConsumer;
        this.parser = parser;
        this.nameConsumer = nameConsumer;
        this.playerConsumer = playerConsumer;
    }

    @Override
    public List<SteamEvent> getEvents() {
        try {
            String jsonRank = rankConsumer.fetchTopGames();
            List<String> ids = parser.parseGamesTopId(jsonRank);

            List<SteamEvent> events = buildEvents(ids);

            events.sort(Comparator.comparingInt((SteamEvent event) -> event.getPlayers() != null ? event.getPlayers() : 0).reversed());

            return events;

        } catch (Exception e) {
            logger.error("Error al obtener datos de Steam: {}", e.getMessage());
            return List.of();
        }
    }

    private List<SteamEvent> buildEvents(List<String> ids) {
        List<SteamEvent> events = new ArrayList<>();
        String capturedAt = Instant.now().toString();

        for (String id : ids) {
            if (isBlacklisted(id)) continue;

            SteamEvent event = buildEvent(id, capturedAt);

            if (event != null && event.hasPlayers()) {
                events.add(event);
            }
        }

        return events;
    }

    private SteamEvent buildEvent(String id, String capturedAt) {
        String name = nameConsumer.getGameName(id);
        if (name == null) return null;

        Integer players = playerConsumer.getCurrentPlayers(id);

        return new SteamEvent(
                capturedAt,
                SOURCE,
                id,
                name,
                players
        );
    }

    private boolean isBlacklisted(String id) {
        return BLACKLIST.contains(id);
    }
}



/*
public class SteamApiConsumer implements SteamConsumer {

    private static final String SOURCE = "steam-feeder";
    private final SteamRankConsumer rankConsumer;
    private final SteamParser parser;
    private final SteamGameNamesConsumer nameConsumer;
    private final SteamCurrentPlayerConsumer playerConsumer;

    private static final Set<String> BLACKLIST = Set.of("431960", "480", "1366800", "1905180", "218", "1325860", "365670", "4333400", "1812620");

    public SteamApiConsumer(SteamRankConsumer rankConsumer, SteamParser parser, SteamGameNamesConsumer nameConsumer, SteamCurrentPlayerConsumer playerConsumer) {
        this.rankConsumer = rankConsumer;
        this.parser = parser;
        this.nameConsumer = nameConsumer;
        this.playerConsumer = playerConsumer;
    }

    @Override
    public List<SteamEvent> getEvents() {
        try {
            String jsonRank = rankConsumer.fetchTopGames();
            List<String> ids = parser.parseGamesTopId(jsonRank);

            List<SteamGame> games = buildGames(ids);

            games.sort(Comparator.comparingInt(
                    (SteamGame g) -> g.getCurrentPlayers() != null ? g.getCurrentPlayers() : 0
            ).reversed());

            return games;

        } catch (Exception e) {
            logger.error("Error al obtener datos de Steam: {}", e.getMessage());
            return List.of();
        }
    }

    private List<SteamGame> buildGames(List<String> ids) {
        List<SteamGame> games = new ArrayList<>();

        for (String id : ids) {
            if (isBlacklisted(id)) continue;

            SteamGame game = buildGame(id);
            if (game != null) {
                games.add(game);
            }
        }

        return games;
    }

    private SteamGame buildGame(String id) {
        String name = nameConsumer.getGameName(id);
        if (name == null) return null;

        Integer players = playerConsumer.getCurrentPlayers(id);

        return new SteamGame(id, name, players);
    }

    private boolean isBlacklisted(String id) {
        return BLACKLIST.contains(id);
    }
}

 */

