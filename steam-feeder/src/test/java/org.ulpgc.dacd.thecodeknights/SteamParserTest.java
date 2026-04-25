package org.ulpgc.dacd.thecodeknights;

import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.model.SteamParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SteamParserTest {

    @Test
    void shouldParseGameIdsCorrectly() {
        // GIVEN
        String json = """
        {
          "response": {
            "ranks": [
              {"appid": 730},
              {"appid": 570}
            ]
          }
        }
        """;

        SteamParser parser = new SteamParser();

        // WHEN
        List<String> ids = parser.parseGamesTopId(json);

        // THEN
        assertEquals(2, ids.size());
        assertEquals("730", ids.get(0));
        assertEquals("570", ids.get(1));
    }

    @Test
    void shouldReturnEmptyListWhenNoRanks() {
        // GIVEN
        String json = """
        {
          "response": {
            "ranks": []
          }
        }
        """;

        SteamParser parser = new SteamParser();

        // WHEN
        List<String> ids = parser.parseGamesTopId(json);

        // THEN
        assertTrue(ids.isEmpty());
    }
}
