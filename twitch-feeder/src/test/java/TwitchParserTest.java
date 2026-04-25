import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.model.Stream;
import org.ulpgc.dacd.thecodeknights.model.TwitchParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwitchParserTest {

    @Test
    void parseStreams_shouldConvertJsonToStreams() {

        String json = """
        {
          "data": [
            {
              "id": "1",
              "user_name": "user1",
              "game_id": "game1",
              "title": "title1",
              "viewer_count": 100
            },
            {
              "id": "2",
              "user_name": "user2",
              "game_id": "game2",
              "title": "title2",
              "viewer_count": 200
            }
          ]
        }
        """;

        TwitchParser parser = new TwitchParser();

        List<Stream> streams = parser.parseStreams(json);

        assertEquals(2, streams.size());
        assertEquals("user1", streams.get(0).getUserName());
        assertEquals(100, streams.get(0).getViewerCount());
    }
}