package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import com.wilaya.signalement_service.model.NiveauGravite;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreerSignalementResponseTest {

    @Test
    void testDepuisSignalement() {
        Signalement signalement = new Signalement(
                "AB123456",
                TypeIntervention.VOIRIE,
                "Description",
                "url",
                "Zone A",
                NiveauGravite.MOYENNE
        );

        CreerSignalementResponse response = CreerSignalementResponse.depuis(signalement);

        assertNotNull(response);
        assertEquals(signalement.getNumeroSuivi(), response.numeroSuivi());
        assertNotNull(response.message());
        assertEquals("Votre signalement a bien ete enregistre. Conservez precieusement ce numero de suivi pour consulter son etat.", response.message());
    }
}