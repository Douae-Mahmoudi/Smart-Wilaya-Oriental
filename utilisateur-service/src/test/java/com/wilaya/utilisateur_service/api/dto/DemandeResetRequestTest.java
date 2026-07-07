package com.wilaya.utilisateur_service.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DemandeResetRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    @Test
    void requeteValideNeDeclencheAucuneViolation() {
        var request = new DemandeResetRequest("karim@example.com");

        Set<ConstraintViolation<DemandeResetRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void emailVideEstInvalide() {
        var request = new DemandeResetRequest("");

        Set<ConstraintViolation<DemandeResetRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void emailNullEstInvalide() {
        var request = new DemandeResetRequest(null);

        Set<ConstraintViolation<DemandeResetRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void emailSansArobaseEstInvalide() {
        var request = new DemandeResetRequest("pas-un-email");

        Set<ConstraintViolation<DemandeResetRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}















