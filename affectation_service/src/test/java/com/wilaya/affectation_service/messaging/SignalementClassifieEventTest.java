package com.wilaya.affectation_service.messaging;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignalementClassifieEventTest {

    @Test
    void constructeur_devraitAffecterCorrectementLesChamps() {
        UUID signalementId = UUID.randomUUID();
        String numeroSuivi = "SUIVI-2026-001";
        String type = "Voirie";
        String zone = "Zone Nord";
        String gravite = "Elevee";
        LocalDateTime dateClassification = LocalDateTime.now();
        String description = "Nid de poule dangereux";
        String adresse = "12 Rue Test";

        SignalementClassifieEvent event = new SignalementClassifieEvent(
                signalementId, numeroSuivi, type, zone, gravite, dateClassification, description, adresse
        );

        assertEquals(signalementId, event.signalementId());
        assertEquals(numeroSuivi, event.numeroSuivi());
        assertEquals(type, event.type());
        assertEquals(zone, event.zone());
        assertEquals(gravite, event.gravite());
        assertEquals(dateClassification, event.dateClassification());
        assertEquals(description, event.description());
        assertEquals(adresse, event.adresse());
    }

    @Test
    void equals_devraitRetournerTrue_pourDeuxEventsAvecMemesValeurs() {
        UUID signalementId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        SignalementClassifieEvent event1 = new SignalementClassifieEvent(
                signalementId, "SUIVI-001", "Voirie", "Zone Nord", "Elevee", date, "Description", "Adresse"
        );
        SignalementClassifieEvent event2 = new SignalementClassifieEvent(
                signalementId, "SUIVI-001", "Voirie", "Zone Nord", "Elevee", date, "Description", "Adresse"
        );

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void equals_devraitRetournerFalse_pourDeuxEventsAvecValeursDifferentes() {
        SignalementClassifieEvent event1 = new SignalementClassifieEvent(
                UUID.randomUUID(), "SUIVI-001", "Voirie", "Zone Nord", "Elevee", LocalDateTime.now(),
                "Description 1", "Adresse 1"
        );
        SignalementClassifieEvent event2 = new SignalementClassifieEvent(
                UUID.randomUUID(), "SUIVI-002", "Eclairage", "Zone Sud", "Faible", LocalDateTime.now(),
                "Description 2", "Adresse 2"
        );

        assertNotEquals(event1, event2);
    }

    @Test
    void toString_devraitContenirLesValeursDesChamps() {
        UUID signalementId = UUID.randomUUID();
        String numeroSuivi = "SUIVI-2026-001";
        String type = "Voirie";

        SignalementClassifieEvent event = new SignalementClassifieEvent(
                signalementId, numeroSuivi, type, "Zone Nord", "Elevee", LocalDateTime.now(),
                "Description", "Adresse"
        );

        String result = event.toString();

        assertTrue(result.contains(signalementId.toString()));
        assertTrue(result.contains(numeroSuivi));
        assertTrue(result.contains(type));
    }
}























































































