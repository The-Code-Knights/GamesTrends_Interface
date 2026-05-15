package org.ulpgc.dacd.thecodeknights;

import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.thecodeknights.controller.SteamParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SteamParserTest {

    private final SteamParser parser = new SteamParser();

    @Test
    void parseGamesTopId_validJson_returnsIds() {
        String json = """
            {"response":{"ranks":[{"appid":730},{"appid":570}]}}
            """;
        List<String> ids = parser.parseGamesTopId(json);
        assertEquals(2, ids.size());
        assertEquals("730", ids.get(0));
        assertEquals("570", ids.get(1));
    }

    @Test
    void parseGamesTopId_emptyRanks_returnsEmptyList() {
        String json = """
            {"response":{"ranks":[]}}
            """;
        assertTrue(parser.parseGamesTopId(json).isEmpty());
    }

    @Test
    void parseGameName_validGame_returnsName() {
        String json = """
            {"730":{"success":true,"data":{"type":"game","name":"Counter-Strike 2"}}}
            """;
        assertEquals("Counter-Strike 2", parser.parseGameName(json, "730"));
    }

    @Test
    void parseGameName_notGameType_returnsNull() {
        String json = """
            {"730":{"success":true,"data":{"type":"dlc","name":"Some DLC"}}}
            """;
        assertNull(parser.parseGameName(json, "730"));
    }

    @Test
    void parseGameName_unsuccess_returnsNull() {
        String json = """
            {"730":{"success":false}}
            """;
        assertNull(parser.parseGameName(json, "730"));
    }

    @Test
    void parseCurrentPlayers_validJson_returnsCount() {
        String json = """
            {"response":{"player_count":15000}}
            """;
        assertEquals(15000, parser.parseCurrentPlayers(json));
    }

    @Test
    void parseCurrentPlayers_missingField_returnsNull() {
        String json = """
            {"response":{}}
            """;
        assertNull(parser.parseCurrentPlayers(json));
    }
}
