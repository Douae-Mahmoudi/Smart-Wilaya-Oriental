package com.wilaya.affectation_service.messaging;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NotificationEventTest {

    @Test
    void constructeur_devraitAffecterCorrectementLesChamps() {
        // Arrange
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();
        String message = "Une équipe vous a été affectée.";

        // Act
        NotificationEvent event = new NotificationEvent(idEquipe, idSignalement, message);

        // Assert
        assertEquals(idEquipe, event.idEquipe());
        assertEquals(idSignalement, event.idSignalement());
        assertEquals(message, event.message());
    }

    @Test
    void equals_devraitRetournerTrue_pourDeuxEventsAvecMemesValeurs() {
        // Arrange
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();
        String message = "Message identique";

        NotificationEvent event1 = new NotificationEvent(idEquipe, idSignalement, message);
        NotificationEvent event2 = new NotificationEvent(idEquipe, idSignalement, message);

        // Assert
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void equals_devraitRetournerFalse_pourDeuxEventsAvecValeursDifferentes() {
        // Arrange
        NotificationEvent event1 = new NotificationEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Message 1");
        NotificationEvent event2 = new NotificationEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Message 2");

        // Assert
        assertNotEquals(event1, event2);
    }

    @Test
    void toString_devraitContenirLesValeursDesChamps() {
        // Arrange
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();
        String message = "Message de test";

        NotificationEvent event = new NotificationEvent(idEquipe, idSignalement, message);

        // Act
        String result = event.toString();

        // Assert
        assertEquals(true, result.contains(idEquipe.toString()));
        assertEquals(true, result.contains(idSignalement.toString()));
        assertEquals(true, result.contains(message));
    }
}

