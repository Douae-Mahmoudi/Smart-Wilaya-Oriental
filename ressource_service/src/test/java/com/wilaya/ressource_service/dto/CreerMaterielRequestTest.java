package com.wilaya.ressource_service.dto;

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

class CreerMaterielRequestTest {

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
        CreerMaterielRequest request = new CreerMaterielRequest("Camion citerne", UUID.randomUUID());

        Set<ConstraintViolation<CreerMaterielRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void devrait_rejeter_un_type_vide() {
        CreerMaterielRequest request = new CreerMaterielRequest("", UUID.randomUUID());

        Set<ConstraintViolation<CreerMaterielRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("type"));
    }

    @Test
    void devrait_rejeter_un_type_null() {
        CreerMaterielRequest request = new CreerMaterielRequest(null, UUID.randomUUID());

        Set<ConstraintViolation<CreerMaterielRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void devrait_rejeter_un_id_equipe_associee_null() {
        CreerMaterielRequest request = new CreerMaterielRequest("Camion citerne", null);

        Set<ConstraintViolation<CreerMaterielRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("idEquipeAssociee"));
    }
}