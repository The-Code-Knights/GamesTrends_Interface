package org.ulpgc.dacd.thecodeknights.event;

import org.ulpgc.dacd.thecodeknights.model.SteamGame;

public class SteamEventMapper {

    private static final String SOURCE = "steam-feeder";

    public static SteamEvent from(SteamGame game, String capturedAt) {
        return new SteamEvent(
                capturedAt,
                SOURCE,
                game.getAppId(),
                game.getName(),
                game.getCurrentPlayers());
    }
}