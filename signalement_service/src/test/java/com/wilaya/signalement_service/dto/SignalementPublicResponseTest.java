package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignalementPublicResponseTest {

    @Test
    void testDepuisSignalement() {
        Signalement signalement = new Signalement(
                "AB123456",
                TypeIntervention.VOIRIE,
                "Description test",
                "photo.jpg",
                "Zone Nord",
                NiveauGravite.HAUTE,
                "",
                34.68,
                -1.90
        );

        SignalementPublicResponse response = SignalementPublicResponse.depuis(signalement);

        assertNotNull(response);
        assertEquals(signalement.getNumeroSuivi(), response.numeroSuivi());
        assertEquals(signalement.getType(), response.type());
        assertEquals(signalement.getDescription(), response.description());
        assertEquals(signalement.getZone(), response.zone());
        assertEquals(signalement.getLatitude(), response.latitude());
        assertEquals(signalement.getLongitude(), response.longitude());
        assertEquals(signalement.getGravite(), response.gravite());
        assertEquals(signalement.getStatut(), response.statut());
        assertEquals(signalement.getDateCreation(), response.dateCreation());
        assertEquals("AB****56", response.cinMasque());
    }
}









































































