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

class ChangerMotDePasseRequestTest {

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
        var request = new ChangerMotDePasseRequest("ancienMdp1", "nouveauMdp123");

        Set<ConstraintViolation<ChangerMotDePasseRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void ancienMotDePasseVideEstInvalide() {
        var request = new ChangerMotDePasseRequest("", "nouveauMdp123");

        Set<ConstraintViolation<ChangerMotDePasseRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ancienMotDePasse"));
    }

    @Test
    void ancienMotDePasseNullEstInvalide() {
        var request = new ChangerMotDePasseRequest(null, "nouveauMdp123");

        Set<ConstraintViolation<ChangerMotDePasseRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ancienMotDePasse"));
    }

    @Test
    void nouveauMotDePasseTropCourtEstInvalide() {
        var request = new ChangerMotDePasseRequest("ancienMdp1", "court1");

        Set<ConstraintViolation<ChangerMotDePasseRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nouveauMotDePasse"));
    }

    @Test
    void nouveauMotDePasseDExactement8CaracteresEstValide() {
        var request = new ChangerMotDePasseRequest("ancienMdp1", "12345678");

        Set<ConstraintViolation<ChangerMotDePasseRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nouveauMotDePasseVideEstInvalide() {
        var request = new ChangerMotDePasseRequest("ancienMdp1", "");

        Set<ConstraintViolation<ChangerMotDePasseRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nouveauMotDePasse"));
    }
}
