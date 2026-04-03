package org.ulpgc.dacd.thecodeknights;

import java.util.List;

public class SteamController {
    private final SteamConsumer feeder;
    private final SteamSerializerInterface serializer;

    public SteamController(SteamConsumer feeder, SteamSerializerInterface serializer) {
        this.feeder = feeder;
        this.serializer = serializer;
    }

    public void execute() {
        List<SteamGame> games = feeder.getGames();
        serializer.save(games);
    }
}
