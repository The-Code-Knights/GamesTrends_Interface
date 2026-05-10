package fakes;

import org.ulpgc.dacd.thecodeknights.control.provider.TwitchConsumer;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import java.util.List;

public class FakeTwitchConsumer implements TwitchConsumer {

    @Override
    public List<TwitchEvent> fetchEvents(int totalEvents) {
        return List.of(
                new TwitchEvent("ts1", "fake", "1", "user1", "game1", "Game One", "title1", 100),
                new TwitchEvent("ts2", "fake", "2", "user2", "game2", "Game Two", "title2", 200)
        );
    }

    @Override
    public int getApiCallCount() {
        return 1;
    }
}
