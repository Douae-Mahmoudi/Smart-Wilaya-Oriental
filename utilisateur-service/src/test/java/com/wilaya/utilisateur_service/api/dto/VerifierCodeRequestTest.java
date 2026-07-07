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

class VerifierCodeRequestTest {

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
        var request = new VerifierCodeRequest("karim@example.com", "123456", "nouveauMdp123");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void emailInvalideEstRejete() {
        var request = new VerifierCodeRequest("pas-un-email", "123456", "nouveauMdp123");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void emailVideEstInvalide() {
        var request = new VerifierCodeRequest("", "123456", "nouveauMdp123");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void codeTropCourtEstInvalide() {
        var request = new VerifierCodeRequest("karim@example.com", "123", "nouveauMdp123");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("code"));
    }

    @Test
    void codeTropLongEstInvalide() {
        var request = new VerifierCodeRequest("karim@example.com", "1234567", "nouveauMdp123");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("code"));
    }

    @Test
    void codeVideEstInvalide() {
        var request = new VerifierCodeRequest("karim@example.com", "", "nouveauMdp123");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("code"));
    }

    @Test
    void codeDExactement6ChiffresEstValide() {
        var request = new VerifierCodeRequest("karim@example.com", "654321", "nouveauMdp123");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nouveauMotDePasseTropCourtEstInvalide() {
        var request = new VerifierCodeRequest("karim@example.com", "123456", "court1");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nouveauMotDePasse"));
    }

    @Test
    void nouveauMotDePasseVideEstInvalide() {
        var request = new VerifierCodeRequest("karim@example.com", "123456", "");

        Set<ConstraintViolation<VerifierCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nouveauMotDePasse"));
    }
}















