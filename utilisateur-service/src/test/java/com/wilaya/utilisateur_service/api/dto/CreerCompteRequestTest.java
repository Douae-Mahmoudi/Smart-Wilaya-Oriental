package com.wilaya.utilisateur_service.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreerCompteRequestTest {

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
    void requeteValideAvecIdEquipeNeDeclencheAucuneViolation() {
        var request = new CreerCompteRequest("Benali", "Karim", "karim@example.com", "0600000000", UUID.randomUUID());

        Set<ConstraintViolation<CreerCompteRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void idEquipeNullEstAccepte() {

        var request = new CreerCompteRequest("Alaoui", "Yassine", "yassine@example.com", "0611111111", null);

        Set<ConstraintViolation<CreerCompteRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nomVideEstInvalide() {
        var request = new CreerCompteRequest("", "Yassine", "yassine@example.com", "0611111111", null);

        Set<ConstraintViolation<CreerCompteRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void prenomVideEstInvalide() {
        var request = new CreerCompteRequest("Alaoui", "", "yassine@example.com", "0611111111", null);

        Set<ConstraintViolation<CreerCompteRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prenom"));
    }

    @Test
    void emailInvalideEstRejete() {
        var request = new CreerCompteRequest("Alaoui", "Yassine", "pas-un-email", "0611111111", null);

        Set<ConstraintViolation<CreerCompteRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void emailVideEstInvalide() {
        var request = new CreerCompteRequest("Alaoui", "Yassine", "", "0611111111", null);

        Set<ConstraintViolation<CreerCompteRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void telephoneNullEstAccepte() {
        var request = new CreerCompteRequest("Alaoui", "Yassine", "yassine@example.com", null, null);

        Set<ConstraintViolation<CreerCompteRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
