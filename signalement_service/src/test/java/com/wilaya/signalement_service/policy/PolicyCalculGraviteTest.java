package com.wilaya.signalement_service.policy;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PolicyCalculGraviteTest {

    private PolicyCalculGravite policy;

    @BeforeEach
    void setUp() {
        policy = new PolicyCalculGravite();
    }

    @ParameterizedTest
    @CsvSource({
            "EAU, hopital, HAUTE",
            "ELECTRICITE, ecole, HAUTE",
            "EAU, Rue standard, MOYENNE",
            "VOIRIE, hopital, MOYENNE",
            "VOIRIE, Rue standard, BASSE",
            "PROPRETE, , BASSE",
            "ESPACES_VERTS, Lycée, MOYENNE"
    })
    void testCalculGravite(TypeIntervention type, String zone, NiveauGravite resultatAttendu) {
        assertEquals(resultatAttendu, policy.calculer(type, zone));
    }

    @Test
    void testCalculTypeNullLanceException() {
        assertThrows(IllegalArgumentException.class, () -> policy.calculer(null, "zone"));
    }
}