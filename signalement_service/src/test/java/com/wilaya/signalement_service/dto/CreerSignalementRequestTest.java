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
                "123 Rue quods ",
                34.68,
                -1.90
        );
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testInvalidFields() {
        CreerSignalementRequest request = new CreerSignalementRequest(
                "",
                null,
                "",
                null,
                "",
                null,
                null
        );
        var violations = validator.validate(request);

        assertEquals(6, violations.size());
    }

    @Test
    void testBlankFields() {
        CreerSignalementRequest request = new CreerSignalementRequest(
                " ",
                TypeIntervention.EAU,
                " ",
                " ",
                " ",
                34.68,
                -1.90
        );
        var violations = validator.validate(request);

        assertEquals(3, violations.size());
    }
}






































