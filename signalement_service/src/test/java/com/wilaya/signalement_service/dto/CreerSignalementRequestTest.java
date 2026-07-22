package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.TypeIntervention;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreerSignalementRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest() {
        CreerSignalementRequest request = new CreerSignalementRequest(
                "AB123456",
                TypeIntervention.VOIRIE,
                "Nid de poule sur la route",
                "Zone Nord",
                "123 Rue quods "
        );
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testInvalidFields() {
        CreerSignalementRequest request = new CreerSignalementRequest(
                "",    // cin
                null,  // type
                "",    // description
                null,  // zone
                ""     // adresse
        );
        var violations = validator.validate(request);
        // Vous avez maintenant 5 champs invalidés (cin, type, description, zone, adresse)
        assertEquals(5, violations.size());
    }

    @Test
    void testBlankFields() {
        // " " est considéré comme blank, donc cin, description, zone, adresse sont invalidés (4 champs)
        CreerSignalementRequest request = new CreerSignalementRequest(
                " ",
                TypeIntervention.EAU,
                " ",
                " ",
                " "
        );
        var violations = validator.validate(request);
        // cin, description, zone, adresse => 4 violations
        assertEquals(4, violations.size());
    }
}