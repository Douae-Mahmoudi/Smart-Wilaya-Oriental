package com.wilaya.signalement_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatistiquesSignalementResponseTest {

    @Test
    void testConstructeurEtAccesseurs() {
        StatistiquesSignalementResponse response = new StatistiquesSignalementResponse(100L, 40L, 55L, 5L);

        assertEquals(100L, response.total());
        assertEquals(40L, response.enCours());
        assertEquals(55L, response.resolus());
        assertEquals(5L, response.critiques());
    }

    @Test
    void testEquals_avecMemesValeurs() {
        StatistiquesSignalementResponse r1 = new StatistiquesSignalementResponse(100L, 40L, 55L, 5L);
        StatistiquesSignalementResponse r2 = new StatistiquesSignalementResponse(100L, 40L, 55L, 5L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testEquals_avecValeursDifferentes() {
        StatistiquesSignalementResponse r1 = new StatistiquesSignalementResponse(100L, 40L, 55L, 5L);
        StatistiquesSignalementResponse r2 = new StatistiquesSignalementResponse(80L, 30L, 45L, 3L);

        assertNotEquals(r1, r2);
    }

    @Test
    void testToString_contientLesValeurs() {
        StatistiquesSignalementResponse response = new StatistiquesSignalementResponse(100L, 40L, 55L, 5L);

        String result = response.toString();

        assertTrue(result.contains("100"));
        assertTrue(result.contains("40"));
        assertTrue(result.contains("55"));
        assertTrue(result.contains("5"));
    }
}
