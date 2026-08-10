package com.wilaya.signalement_service.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChangerStatutRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest() {
        ChangerStatutRequest request = new ChangerStatutRequest("SIGNALE", "Prise en charge par l'agent");
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void testBlankStatutFails() {
        ChangerStatutRequest request = new ChangerStatutRequest("", "Un message valide");
        var violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Le statut est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void testNullStatutFails() {
        ChangerStatutRequest request = new ChangerStatutRequest(null, "Un message valide");
        var violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Le statut est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankMessageFails() {
        ChangerStatutRequest request = new ChangerStatutRequest("SIGNALE", "");
        var violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Un message explicatif est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void testNullMessageFails() {
        ChangerStatutRequest request = new ChangerStatutRequest("SIGNALE", null);
        var violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Un message explicatif est obligatoire", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankStatutAndMessageFails() {
        ChangerStatutRequest request = new ChangerStatutRequest("", "");
        var violations = validator.validate(request);
        assertEquals(2, violations.size());
    }
}
