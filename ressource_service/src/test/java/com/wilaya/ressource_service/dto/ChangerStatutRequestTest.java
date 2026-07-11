package com.wilaya.ressource_service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ChangerStatutRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void devrait_valider_une_requete_correcte() {
        ChangerStatutRequest request = new ChangerStatutRequest("EN_INTERVENTION");

        Set<ConstraintViolation<ChangerStatutRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void devrait_rejeter_un_statut_vide() {
        ChangerStatutRequest request = new ChangerStatutRequest("");

        Set<ConstraintViolation<ChangerStatutRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("statut"));
    }

    @Test
    void devrait_rejeter_un_statut_null() {
        ChangerStatutRequest request = new ChangerStatutRequest(null);

        Set<ConstraintViolation<ChangerStatutRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }
}