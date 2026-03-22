package org.ulpgc.dacd.thecodeknights;

import java.util.List;


public class Main {

    public static void main(String[] args) {
        try {
            // 1. Obtener lista de juegos
            SteamApiClient client = new SteamApiClient();
            String json = client.fetchTopGames();

            // 2. Parsear juegos
            SteamService service = new SteamService();
            List<SteamGame> games = service.parseGames(json);

            System.out.println("\nJuegos obtenidos:\n");
            games.sort((g1, g2) -> Integer.compare(g2.currentPlayers, g1.currentPlayers));

            // ⚠️ IMPORTANTE: limitar para no hacer demasiadas llamadas
            for (int i = 0; i < Math.min(10, games.size()); i++) {

                SteamGame g = games.get(i);

                try {
                    // 3. Obtener nombre
                    SteamGameDetailsService detailsService =
                            new SteamGameDetailsService(g.appId);
                    g.name = detailsService.getGameName();

                    // 4. Obtener jugadores actuales
                    SteamCurrentPlayer playerService =
                            new SteamCurrentPlayer(g.appId);
                    g.currentPlayers = playerService.getCurrentPlayers();

                    // 5. Mostrar resultado final
                    System.out.println(
                            "Name: " + g.name +
                                    " | Current Players: " + g.currentPlayers
                    );

                } catch (RuntimeException e) {
                    System.err.println(
                            "Error con juego " + g.appId + ": " + e.getMessage()
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
        }
    }
}

//"https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=570";
//https://api.steampowered.com/ISteamChartsService/GetMostPlayedGames/v1/

