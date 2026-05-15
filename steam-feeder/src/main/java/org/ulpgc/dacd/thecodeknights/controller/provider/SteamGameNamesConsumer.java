package org.ulpgc.dacd.thecodeknights.controller.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ulpgc.dacd.thecodeknights.controller.SteamParser;

public class SteamGameNamesConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SteamGameNamesConsumer.class);

    private final SteamHttpGestor httpClient;
    private final SteamParser parser;

    public SteamGameNamesConsumer(SteamHttpGestor httpClient, SteamParser parser) {
        this.httpClient = httpClient;
        this.parser = parser;
    }

    public String getGameName(String appId) {
        try {
            String url = "https://store.steampowered.com/api/appdetails?appids=" + appId;
            String json = httpClient.fetchJson(url);

            return parser.parseGameName(json, appId);

        } catch (Exception e) {
            logger.error("Error obteniendo nombre para appId {}: {}", appId, e.getMessage());
            return null;
        }
    }
}
