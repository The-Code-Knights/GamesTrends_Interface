import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.control.TwitchParser;
import org.ulpgc.dacd.thecodeknights.model.TwitchEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TwitchParserTest {

    private final TwitchParser parser = new TwitchParser();

    @Test
    void parseEvents_validJson_returnsEvents() {
        String json = """
            {"data":[
                {"id":"1","user_name":"streamer1","game_id":"g1","title":"Playing CS2","viewer_count":5000},
                {"id":"2","user_name":"streamer2","game_id":"g2","title":"Playing Dota","viewer_count":3000}
            ],"pagination":{}}
            """;
        List<TwitchEvent> events = parser.parseEvents(json);
        assertEquals(2, events.size());
        assertEquals("streamer1", events.get(0).getUserName());
        assertEquals(5000, events.get(0).getViewerCount());
        assertEquals("1", events.get(0).getStreamId());
    }

    @Test
    void parseEvents_emptyData_returnsEmptyList() {
        String json = """
            {"data":[],"pagination":{}}
            """;
        assertTrue(parser.parseEvents(json).isEmpty());
    }

    @Test
    void extractCursor_cursorPresent_returnsCursor() {
        String json = """
            {"data":[],"pagination":{"cursor":"abc123"}}
            """;
        assertEquals("abc123", parser.extractCursor(json));
    }

    @Test
    void extractCursor_cursorAbsent_returnsNull() {
        String json = """
            {"data":[],"pagination":{}}
            """;
        assertNull(parser.extractCursor(json));
    }
}
