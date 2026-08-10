package com.wilaya.signalement_service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatutActuelResponseTest {

    @Test
    void testConstructeurEtAccesseur() {
        StatutActuelResponse response = new StatutActuelResponse("CLASSIFIE");

        assertEquals("CLASSIFIE", response.statut());
    }

    @Test
    void testEquals_avecMemeValeur() {
        StatutActuelResponse r1 = new StatutActuelResponse("CLASSIFIE");
        StatutActuelResponse r2 = new StatutActuelResponse("CLASSIFIE");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testEquals_avecValeurDifferente() {
        StatutActuelResponse r1 = new StatutActuelResponse("CLASSIFIE");
        StatutActuelResponse r2 = new StatutActuelResponse("RESOLU");

        assertNotEquals(r1, r2);
    }

    @Test
    void testToString_contientLaValeur() {
        StatutActuelResponse response = new StatutActuelResponse("EN_INTERVENTION");

        assertTrue(response.toString().contains("EN_INTERVENTION"));
    }
}















































































