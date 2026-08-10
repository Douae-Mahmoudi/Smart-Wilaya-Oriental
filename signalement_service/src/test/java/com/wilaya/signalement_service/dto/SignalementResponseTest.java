package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignalementResponseTest {

    @Test
    void testDepuisSignalement() {
        Signalement signalement = new Signalement(
                "AB123456",
                TypeIntervention.EAU,
                "Fuite d'eau",
                "http://photo.url",
                "Quartier Centre",
                NiveauGravite.HAUTE,
                "12 Rue Test",
                34.68,
                -1.90
        );

        SignalementResponse response = SignalementResponse.depuis(signalement);

        assertNotNull(response);
        assertEquals(signalement.getId(), response.id());
        assertEquals(signalement.getNumeroSuivi(), response.numeroSuivi());
        assertEquals(signalement.getCinDeclarant(), response.cinDeclarant());
        assertEquals(signalement.getType(), response.type());
        assertEquals(signalement.getDescription(), response.description());
        assertEquals(signalement.getPhotoUrl(), response.photoUrl());
        assertEquals(signalement.getZone(), response.zone());
        assertEquals(signalement.getAdresse(), response.adresse());
        assertEquals(signalement.getLatitude(), response.latitude());
        assertEquals(signalement.getLongitude(), response.longitude());
        assertEquals(signalement.getGravite(), response.gravite());
        assertEquals(signalement.getStatut(), response.statut());
        assertEquals(signalement.getDateCreation(), response.dateCreation());
    }
}









































































