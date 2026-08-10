package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignalementCarteResponseTest {

    @Test
    void testDepuisSignalement() {
        Signalement signalement = new Signalement(
                "AB123456",
                TypeIntervention.EAU,
                "Fuite d'eau",
                "photo.jpg",
                "Zone Nord",
                NiveauGravite.HAUTE,
                "12 Rue Test",
                34.68,
                -1.90
        );

        SignalementCarteResponse response = SignalementCarteResponse.depuis(signalement);

        assertNotNull(response);
        assertEquals(signalement.getId(), response.id());
        assertEquals(signalement.getNumeroSuivi(), response.numeroSuivi());
        assertEquals(signalement.getLatitude(), response.latitude());
        assertEquals(signalement.getLongitude(), response.longitude());
        assertEquals(signalement.getType(), response.type());
        assertEquals(signalement.getGravite(), response.gravite());
        assertEquals(signalement.getStatut(), response.statut());
    }
}















































































