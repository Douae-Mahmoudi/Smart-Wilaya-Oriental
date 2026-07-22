package com.wilaya.signalement_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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
                "123 rue el Quods "
        );
    }

    @Test
    void testInitialisation() {
        assertNotNull(signalement.getNumeroSuivi());
        assertEquals(StatutSignalement.SIGNALE, signalement.getStatut());
        assertEquals("AB123456", signalement.getCinDeclarant());
    }

    @Test
    void testChangementStatutValide() {
        assertDoesNotThrow(() -> signalement.changerStatut(StatutSignalement.CLASSIFIE));
        assertEquals(StatutSignalement.CLASSIFIE, signalement.getStatut());
    }

    @Test
    void testChangementStatutInvalide() {
        assertThrows(IllegalStateException.class, () -> signalement.changerStatut(StatutSignalement.RESOLU));
    }

    @Test
    void testClotureDepuisNimporteQuelEtat() {
        signalement.changerStatut(StatutSignalement.CLASSIFIE);
        assertDoesNotThrow(() -> signalement.changerStatut(StatutSignalement.CLOTURE));
        assertEquals(StatutSignalement.CLOTURE, signalement.getStatut());
    }

    @Test
    void testTransitionClotureVersAutreImpossible() {
        signalement.changerStatut(StatutSignalement.CLOTURE);
        assertThrows(IllegalStateException.class, () -> signalement.changerStatut(StatutSignalement.SIGNALE));
    }

    @Test
    void testMasquageCin() {
        assertEquals("AB****56", signalement.masquerCin());

        Signalement court = new Signalement("123", null, null, null, null, null,"");
        assertEquals("****", court.masquerCin());
    }
}