package org.ulpgc.dacd.thecodeknights;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SteamApiConsumer implements SteamConsumer {

    private final SteamRankConsumer rankConsumer;
    private final SteamParser parser;
    private final SteamGameNamesConsumer nameConsumer;
    private final SteamCurrentPlayerConsumer playerConsumer;
    private static final Set<String> BLACKLIST = Set.of("431960", "480", "1366800", "1905180", "218", "1325860", "365670", "4333400", "1812620"
    );

    public SteamApiConsumer() {
        this.rankConsumer = new SteamRankConsumer();
        this.parser = new SteamParser();
        this.nameConsumer = new SteamGameNamesConsumer();
        this.playerConsumer = new SteamCurrentPlayerConsumer();
    }

    @Override
    public List<SteamGame> getGames() {
        List<SteamGame> finalGames = new ArrayList<>();

        try {

            String jsonRank = rankConsumer.fetchTopGames();


            List<SteamGame> BuildGames = parser.parseGames(jsonRank);


            for (SteamGame game : BuildGames) { //Se puede poner alguna condicion para pillar menos juegos

                if (BLACKLIST.contains(game.appId)){
                    continue;
                }
                String name = nameConsumer.getGameName(game.appId);


                if (name != null) {
                    game.name = name;

                    int players = playerConsumer.getCurrentPlayers(game.appId);
                    game.currentPlayers = players;

                    finalGames.add(game);
                }
            }

        } catch (IOException e) {
            System.err.println("Error al obtener datos de Steam: " + e.getMessage());
        }


        finalGames.sort((g1, g2) -> Integer.compare(g2.currentPlayers, g1.currentPlayers));

        return finalGames;
    }
}

