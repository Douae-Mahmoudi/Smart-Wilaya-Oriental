package com.wilaya.signalement_service.messaging;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EquipeAffecteeEventTest {

    @Test
    void testConstructeurEtAccesseurs() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        LocalDateTime dateAcceptation = LocalDateTime.now();

        EquipeAffecteeEvent event = new EquipeAffecteeEvent(idSignalement, idEquipe, dateAcceptation);

        assertEquals(idSignalement, event.idSignalement());
        assertEquals(idEquipe, event.idEquipe());
        assertEquals(dateAcceptation, event.dateAcceptation());
    }

    @Test
    void testEquals_avecMemesValeurs() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        EquipeAffecteeEvent e1 = new EquipeAffecteeEvent(idSignalement, idEquipe, date);
        EquipeAffecteeEvent e2 = new EquipeAffecteeEvent(idSignalement, idEquipe, date);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testEquals_avecValeursDifferentes() {
        EquipeAffecteeEvent e1 = new EquipeAffecteeEvent(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        EquipeAffecteeEvent e2 = new EquipeAffecteeEvent(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());

        assertNotEquals(e1, e2);
    }

    @Test
    void testToString_contientLesValeurs() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        EquipeAffecteeEvent event = new EquipeAffecteeEvent(idSignalement, idEquipe, date);
        String result = event.toString();

        assertTrue(result.contains(idSignalement.toString()));
        assertTrue(result.contains(idEquipe.toString()));
    }
}















































































