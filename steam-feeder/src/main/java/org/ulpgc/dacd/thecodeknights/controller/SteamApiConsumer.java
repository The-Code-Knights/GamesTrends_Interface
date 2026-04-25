package org.ulpgc.dacd.thecodeknights.controller;

import org.ulpgc.dacd.thecodeknights.model.SteamGame;
import org.ulpgc.dacd.thecodeknights.model.SteamParser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

public class SteamApiConsumer implements SteamConsumer {

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
    public List<SteamGame> getGames() {
        try {
            String jsonRank = rankConsumer.fetchTopGames();
            List<String> ids = parser.parseGamesTopId(jsonRank);

            List<SteamGame> games = buildGames(ids);

            games.sort(Comparator.comparingInt((SteamGame g) -> g.getCurrentPlayers().orElse(0)).reversed());

            return games;

        } catch (Exception e) {
            System.err.println("Error al obtener datos de Steam: " + e.getMessage());
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

        OptionalInt players = playerConsumer.getCurrentPlayers(id);

        return new SteamGame(id, name, players);
    }

    private boolean isBlacklisted(String id) {
        return BLACKLIST.contains(id);
    }
}

