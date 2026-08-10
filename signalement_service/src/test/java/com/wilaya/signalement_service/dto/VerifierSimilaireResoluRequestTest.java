package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.TypeIntervention;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerifierSimilaireResoluRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest() {
        VerifierSimilaireResoluRequest request = new VerifierSimilaireResoluRequest(
                TypeIntervention.EAU,
                "Fuite d'eau importante",
                "Zone Nord",
                34.68,
                -1.90
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testValidRequest_sansZoneNiCoordonnees() {
        VerifierSimilaireResoluRequest request = new VerifierSimilaireResoluRequest(
                TypeIntervention.EAU,
                "Fuite d'eau importante",
                null,
                null,
                null
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testTypeNullEtDescriptionVide() {
        VerifierSimilaireResoluRequest request = new VerifierSimilaireResoluRequest(
                null,
                "",
                "Zone Nord",
                34.68,
                -1.90
        );

        var violations = validator.validate(request);

        assertEquals(2, violations.size());
    }

    @Test
    void testDescriptionBlancheSeule() {
        VerifierSimilaireResoluRequest request = new VerifierSimilaireResoluRequest(
                TypeIntervention.VOIRIE,
                "   ",
                "Zone Nord",
                34.68,
                -1.90
        );

        var violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("La description est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void testTypeNullSeul() {
        VerifierSimilaireResoluRequest request = new VerifierSimilaireResoluRequest(
                null,
                "Description valide",
                "Zone Nord",
                34.68,
                -1.90
        );

        var violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Le type d'intervention est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void testDescriptionNulle() {
        VerifierSimilaireResoluRequest request = new VerifierSimilaireResoluRequest(
                TypeIntervention.EAU,
                null,
                "Zone Nord",
                34.68,
                -1.90
        );

        var violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("La description est obligatoire", violations.iterator().next().getMessage());
    }
}
