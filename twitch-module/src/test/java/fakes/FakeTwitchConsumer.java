package fakes;

import org.ulpgc.dacd.thecodeknights.model.Stream;
import org.ulpgc.dacd.thecodeknights.control.TwitchConsumer;

import java.util.List;

public class FakeTwitchConsumer implements TwitchConsumer {

    @Override
    public List<Stream> fetchStreams(int totalStreams) {
        return List.of(
                new Stream("1", "user1", "game1", "title1", 100),
                new Stream("2", "user2", "game2", "title2", 200)
        );
    }

    @Override
    public int getApiCallCount() {
        return 1;
    }
}