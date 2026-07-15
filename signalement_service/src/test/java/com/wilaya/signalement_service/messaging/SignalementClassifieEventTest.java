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

        assertEquals(id, event.getSignalementId());
        assertEquals("SIG-123", event.getNumeroSuivi());
        assertEquals(TypeIntervention.VOIRIE, event.getType());
        assertEquals("Zone A", event.getZone());
        assertEquals(NiveauGravite.MOYENNE, event.getGravite());
        assertEquals(now, event.getDateClassification());
    }

    @Test
    void testConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                id, "SIG-456", TypeIntervention.EAU, "Zone B", NiveauGravite.HAUTE, now
        );

        assertNotNull(event);
        assertEquals(id, event.getSignalementId());
        assertEquals("SIG-456", event.getNumeroSuivi());
        assertEquals(TypeIntervention.EAU, event.getType());
        assertEquals("Zone B", event.getZone());
        assertEquals(NiveauGravite.HAUTE, event.getGravite());
        assertEquals(now, event.getDateClassification());
    }
}