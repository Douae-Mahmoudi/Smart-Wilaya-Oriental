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
        // Arrange
        UUID signalementId = UUID.randomUUID();
        String numeroSuivi = "SUIVI-2026-001";
        String type = "Voirie";
        String zone = "Zone Nord";
        String gravite = "Elevee";
        LocalDateTime dateClassification = LocalDateTime.now();

        // Act
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                signalementId, numeroSuivi, type, zone, gravite, dateClassification
        );

        // Assert
        assertEquals(signalementId, event.signalementId());
        assertEquals(numeroSuivi, event.numeroSuivi());
        assertEquals(type, event.type());
        assertEquals(zone, event.zone());
        assertEquals(gravite, event.gravite());
        assertEquals(dateClassification, event.dateClassification());
    }

    @Test
    void equals_devraitRetournerTrue_pourDeuxEventsAvecMemesValeurs() {
        // Arrange
        UUID signalementId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now();

        SignalementClassifieEvent event1 = new SignalementClassifieEvent(
                signalementId, "SUIVI-001", "Voirie", "Zone Nord", "Elevee", date
        );
        SignalementClassifieEvent event2 = new SignalementClassifieEvent(
                signalementId, "SUIVI-001", "Voirie", "Zone Nord", "Elevee", date
        );

        // Assert
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void equals_devraitRetournerFalse_pourDeuxEventsAvecValeursDifferentes() {
        // Arrange
        SignalementClassifieEvent event1 = new SignalementClassifieEvent(
                UUID.randomUUID(), "SUIVI-001", "Voirie", "Zone Nord", "Elevee", LocalDateTime.now()
        );
        SignalementClassifieEvent event2 = new SignalementClassifieEvent(
                UUID.randomUUID(), "SUIVI-002", "Eclairage", "Zone Sud", "Faible", LocalDateTime.now()
        );

        // Assert
        assertNotEquals(event1, event2);
    }

    @Test
    void toString_devraitContenirLesValeursDesChamps() {
        // Arrange
        UUID signalementId = UUID.randomUUID();
        String numeroSuivi = "SUIVI-2026-001";
        String type = "Voirie";

        SignalementClassifieEvent event = new SignalementClassifieEvent(
                signalementId, numeroSuivi, type, "Zone Nord", "Elevee", LocalDateTime.now()
        );

        // Act
        String result = event.toString();

        // Assert
        assertTrue(result.contains(signalementId.toString()));
        assertTrue(result.contains(numeroSuivi));
        assertTrue(result.contains(type));
    }
}

