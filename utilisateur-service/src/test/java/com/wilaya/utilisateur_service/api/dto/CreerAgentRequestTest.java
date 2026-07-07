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

class CreerAgentRequestTest {

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
        var request = new CreerAgentRequest("Benali", "Karim", "karim@example.com", "0600000000", UUID.randomUUID());

        Set<ConstraintViolation<CreerAgentRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nomVideEstInvalide() {
        var request = new CreerAgentRequest("", "Karim", "karim@example.com", "0600000000", UUID.randomUUID());

        Set<ConstraintViolation<CreerAgentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void prenomVideEstInvalide() {
        var request = new CreerAgentRequest("Benali", "", "karim@example.com", "0600000000", UUID.randomUUID());

        Set<ConstraintViolation<CreerAgentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prenom"));
    }

    @Test
    void emailInvalideEstRejete() {
        var request = new CreerAgentRequest("Benali", "Karim", "pas-un-email", "0600000000", UUID.randomUUID());

        Set<ConstraintViolation<CreerAgentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void emailVideEstInvalide() {
        var request = new CreerAgentRequest("Benali", "Karim", "", "0600000000", UUID.randomUUID());

        Set<ConstraintViolation<CreerAgentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void telephoneNullEstAccepte() {
        // telephone n'a aucune annotation de validation : optionnel
        var request = new CreerAgentRequest("Benali", "Karim", "karim@example.com", null, UUID.randomUUID());

        Set<ConstraintViolation<CreerAgentRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void idEquipeNullEstInvalide() {
        var request = new CreerAgentRequest("Benali", "Karim", "karim@example.com", "0600000000", null);

        Set<ConstraintViolation<CreerAgentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("idEquipe"));
    }
}















