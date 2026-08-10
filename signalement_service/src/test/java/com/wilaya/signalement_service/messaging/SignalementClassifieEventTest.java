package com.wilaya.signalement_service.messaging;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SignalementClassifieEventTest {

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        SignalementClassifieEvent event = new SignalementClassifieEvent();

        event.setSignalementId(id);
        event.setNumeroSuivi("SIG-123");
        event.setType(TypeIntervention.VOIRIE);
        event.setZone("Zone A");
        event.setGravite(NiveauGravite.MOYENNE);
        event.setDateClassification(now);
        event.setDescription("Nid de poule dangereux");
        event.setAdresse("12 Rue Test");

        assertEquals(id, event.getSignalementId());
        assertEquals("SIG-123", event.getNumeroSuivi());
        assertEquals(TypeIntervention.VOIRIE, event.getType());
        assertEquals("Zone A", event.getZone());
        assertEquals(NiveauGravite.MOYENNE, event.getGravite());
        assertEquals(now, event.getDateClassification());
        assertEquals("Nid de poule dangereux", event.getDescription());
        assertEquals("12 Rue Test", event.getAdresse());
    }

    @Test
    void testConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                id, "SIG-456", TypeIntervention.EAU, "Zone B", NiveauGravite.HAUTE, now,
                "Fuite d'eau importante", "34 Avenue Test"
        );

        assertNotNull(event);
        assertEquals(id, event.getSignalementId());
        assertEquals("SIG-456", event.getNumeroSuivi());
        assertEquals(TypeIntervention.EAU, event.getType());
        assertEquals("Zone B", event.getZone());
        assertEquals(NiveauGravite.HAUTE, event.getGravite());
        assertEquals(now, event.getDateClassification());
        assertEquals("Fuite d'eau importante", event.getDescription());
        assertEquals("34 Avenue Test", event.getAdresse());
    }
}
