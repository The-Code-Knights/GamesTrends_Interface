package org.ulpgc.dacd.thecodeknights.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventRouterTest {

    @Test
    void normalizeName_trademark_removed() {
        assertEquals("Hades", EventRouter.normalizeName("Hades™"));
    }

    @Test
    void normalizeName_registered_removed() {
        assertEquals("Call of Duty", EventRouter.normalizeName("Call of Duty®"));
    }


    @Test
    void normalizeName_counterStrike2_mapsToAlias() {
        assertEquals("Counter-Strike", EventRouter.normalizeName("Counter-Strike 2"));
    }

    @Test
    void normalizeName_romanNumerals_mapsToAlias() {
        assertEquals("Red Dead Redemption 2", EventRouter.normalizeName("Red Dead Redemption II"));
        assertEquals("Slay the Spire 2", EventRouter.normalizeName("Slay the Spire II"));
    }

    @Test
    void normalizeName_residentEvil_colonVariant_normalized() {
        assertEquals("Resident Evil Requiem", EventRouter.normalizeName("RESIDENT EVIL: requiem"));
    }

    @Test
    void normalizeName_trailingColon_removed() {
        assertEquals("Dead by Daylight", EventRouter.normalizeName("Dead by Daylight:"));
    }

    @Test
    void normalizeName_multipleSpaces_collapsed() {
        assertEquals("Game Name", EventRouter.normalizeName("Game  Name"));
    }

    @Test
    void normalizeName_null_returnsNull() {
        assertNull(EventRouter.normalizeName(null));
    }

    @Test
    void normalizeName_empty_returnsEmpty() {
        assertEquals("", EventRouter.normalizeName(""));
    }
}
