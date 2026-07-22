package com.wilaya.affectation_service.dto;

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

class AccepterAffectationRequestTest {

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
    void devrait_valider_une_requete_avec_id_equipe_renseigne() {
        AccepterAffectationRequest request = new AccepterAffectationRequest(UUID.randomUUID());

        Set<ConstraintViolation<AccepterAffectationRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void devrait_rejeter_une_requete_avec_id_equipe_null() {
        AccepterAffectationRequest request = new AccepterAffectationRequest(null);

        Set<ConstraintViolation<AccepterAffectationRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("idEquipe"));
    }

    @Test
    void devrait_conserver_lid_equipe_fourni() {
        UUID idEquipe = UUID.randomUUID();

        AccepterAffectationRequest request = new AccepterAffectationRequest(idEquipe);

        assertThat(request.idEquipe()).isEqualTo(idEquipe);
    }
}