package com.wilaya.ressource_service.dto;

import com.wilaya.ressource_service.model.CategorieIntervention;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreerEquipeRequestTest {

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
        CreerEquipeRequest request = new CreerEquipeRequest(
                "Equipe Eau Zone Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        Set<ConstraintViolation<CreerEquipeRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void devrait_rejeter_un_nom_vide() {
        CreerEquipeRequest request = new CreerEquipeRequest(
                "", List.of(CategorieIntervention.EAU), "Zone Nord");

        Set<ConstraintViolation<CreerEquipeRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void devrait_rejeter_un_nom_null() {
        CreerEquipeRequest request = new CreerEquipeRequest(
                null, List.of(CategorieIntervention.EAU), "Zone Nord");

        Set<ConstraintViolation<CreerEquipeRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void devrait_rejeter_une_liste_de_competences_vide() {
        CreerEquipeRequest request = new CreerEquipeRequest(
                "Equipe Eau", List.of(), "Zone Nord");

        Set<ConstraintViolation<CreerEquipeRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("competences"));
    }

    @Test
    void devrait_rejeter_une_liste_de_competences_null() {
        CreerEquipeRequest request = new CreerEquipeRequest(
                "Equipe Eau", null, "Zone Nord");

        Set<ConstraintViolation<CreerEquipeRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void devrait_rejeter_une_zone_couverture_vide() {
        CreerEquipeRequest request = new CreerEquipeRequest(
                "Equipe Eau", List.of(CategorieIntervention.EAU), "");

        Set<ConstraintViolation<CreerEquipeRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("zoneCouverture"));
    }

    @Test
    void devrait_accepter_plusieurs_competences() {
        CreerEquipeRequest request = new CreerEquipeRequest(
                "Equipe Polyvalente",
                List.of(CategorieIntervention.EAU, CategorieIntervention.ELECTRICITE),
                "Zone Sud");

        Set<ConstraintViolation<CreerEquipeRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}