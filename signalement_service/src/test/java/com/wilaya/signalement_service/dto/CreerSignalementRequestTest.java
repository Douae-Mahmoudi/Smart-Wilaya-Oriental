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
                "Zone Nord"
        );
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testInvalidFields() {
        CreerSignalementRequest request = new CreerSignalementRequest(
                "",
                null,
                "",
                null
        );
        var violations = validator.validate(request);
        assertEquals(4, violations.size());
    }

    @Test
    void testBlankFields() {
        CreerSignalementRequest request = new CreerSignalementRequest(" ", TypeIntervention.EAU, " ", " ");
        var violations = validator.validate(request);
        // cin, description, zone
        assertEquals(3, violations.size());
    }
}