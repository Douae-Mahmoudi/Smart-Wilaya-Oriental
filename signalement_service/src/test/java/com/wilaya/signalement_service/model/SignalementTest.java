package com.wilaya.signalement_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignalementTest {

    private Signalement signalement;

    @BeforeEach
    void setUp() {
        signalement = new Signalement(
                "AB123456",
                TypeIntervention.VOIRIE,
                "Description test",
                "url",
                "Zone A",
                NiveauGravite.MOYENNE,
                "123 rue el Quods ",
                34.68,
                -1.90
        );
    }

    @Test
    void testInitialisation() {
        assertNotNull(signalement.getNumeroSuivi());
        assertEquals(StatutSignalement.SIGNALE, signalement.getStatut());
        assertEquals("AB123456", signalement.getCinDeclarant());
    }

    @Test
    void testChangementStatut() {
        assertDoesNotThrow(() -> signalement.changerStatut(StatutSignalement.CLASSIFIE));
        assertEquals(StatutSignalement.CLASSIFIE, signalement.getStatut());
    }

    @Test
    void testMasquageCin() {
        assertEquals("AB****56", signalement.masquerCin());

        Signalement court = new Signalement("123", null, null, null, null, null, "", null, null);
        assertEquals("****", court.masquerCin());
    }
}
