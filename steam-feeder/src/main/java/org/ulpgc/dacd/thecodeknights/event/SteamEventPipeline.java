package org.ulpgc.dacd.thecodeknights.event;

import org.ulpgc.dacd.thecodeknights.model.SteamGame;

import java.time.Instant;
import java.util.List;

public class SteamEventPipeline {

    private final SteamEventPublisher publisher;
    private final SteamEventSerializer serializer;

    public SteamEventPipeline(SteamEventPublisher publisher, SteamEventSerializer serializer) {
        this.publisher = publisher;
        this.serializer = serializer;
    }

    public void process(List<SteamGame> games) {
        String capturedAt = Instant.now().toString();

        for (SteamGame game : games) {
            if (game.getCurrentPlayers() == null) continue;

            SteamEvent event = SteamEventMapper.from(game, capturedAt);
            String json = serializer.serialize(event);

            try {
                publisher.publish(json);
                System.out.println("Evento Steam publicado: " + json);
            } catch (Exception e) {
                System.err.println("Error publicando evento Steam: " + e.getMessage());
            }
        }
    }
}
