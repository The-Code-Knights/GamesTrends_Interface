package org.ulpgc.dacd.thecodeknights.controller.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.controller.SteamParser;

public class SteamCurrentPlayerConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SteamCurrentPlayerConsumer.class);

    private final SteamHttpGestor httpClient;
    private final SteamParser parser;

    public SteamCurrentPlayerConsumer(SteamHttpGestor httpClient, SteamParser parser) {
        this.httpClient = httpClient;
        this.parser = parser;
    }

    public Integer getCurrentPlayers(String appId) {
        try {
            String url = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=" + appId;
            String json = httpClient.fetchJson(url);

            return parser.parseCurrentPlayers(json);

        } catch (Exception e) {
            logger.error("Error obteniendo jugadores para appId {}: {}", appId, e.getMessage());
            return null;
        }
    }
}
