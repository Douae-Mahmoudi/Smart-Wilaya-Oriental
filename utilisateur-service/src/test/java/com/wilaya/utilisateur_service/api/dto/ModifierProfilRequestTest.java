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

class ModifierProfilRequestTest {

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
        var request = new ModifierProfilRequest("Benali", "Karim", "0600000000", true);

        Set<ConstraintViolation<ModifierProfilRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void requeteValideAvecNotificationsDesactiveesNeDeclencheAucuneViolation() {
        var request = new ModifierProfilRequest("Benali", "Karim", "0600000000", false);

        Set<ConstraintViolation<ModifierProfilRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nomVideEstInvalide() {
        var request = new ModifierProfilRequest("", "Karim", "0600000000", true);

        Set<ConstraintViolation<ModifierProfilRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void prenomVideEstInvalide() {
        var request = new ModifierProfilRequest("Benali", "", "0600000000", true);

        Set<ConstraintViolation<ModifierProfilRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prenom"));
    }

    @Test
    void telephoneVideEstInvalide() {
        var request = new ModifierProfilRequest("Benali", "Karim", "", true);

        Set<ConstraintViolation<ModifierProfilRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("telephone"));
    }

    @Test
    void telephoneNullEstInvalide() {
        var request = new ModifierProfilRequest("Benali", "Karim", null, true);

        Set<ConstraintViolation<ModifierProfilRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("telephone"));
    }
}















