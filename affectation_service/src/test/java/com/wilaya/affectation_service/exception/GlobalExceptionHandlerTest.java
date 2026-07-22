package com.wilaya.affectation_service.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void gererEtatInvalide_devrait_retourner_409_avec_le_message() {
        IllegalStateException exception = new IllegalStateException("Impossible d'accepter une tentative ACCEPTEE");

        ResponseEntity<Map<String, Object>> response = handler.gererEtatInvalide(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("status", 409);
        assertThat(response.getBody()).containsEntry("message", "Impossible d'accepter une tentative ACCEPTEE");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void gererArgumentInvalide_devrait_retourner_404_avec_le_message() {
        IllegalArgumentException exception = new IllegalArgumentException("Tentative introuvable");

        ResponseEntity<Map<String, Object>> response = handler.gererArgumentInvalide(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("status", 404);
        assertThat(response.getBody()).containsEntry("message", "Tentative introuvable");
    }

    @Test
    void gererValidationEchouee_devrait_retourner_400_avec_le_detail_des_erreurs_de_champ() {
        FieldError fieldError = new FieldError("accepterAffectationRequest", "idEquipe", "ne doit pas être null");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = handler.gererValidationEchouee(methodArgumentNotValidException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);

        @SuppressWarnings("unchecked")
        Map<String, String> erreurs = (Map<String, String>) response.getBody().get("erreurs");
        assertThat(erreurs).containsEntry("idEquipe", "ne doit pas être null");
    }

    @Test
    void gererValidationEchouee_devrait_regrouper_plusieurs_erreurs_de_champs() {
        FieldError erreur1 = new FieldError("request", "idEquipe", "ne doit pas être null");
        FieldError erreur2 = new FieldError("request", "score", "doit être positif");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(erreur1, erreur2));

        ResponseEntity<Map<String, Object>> response = handler.gererValidationEchouee(methodArgumentNotValidException);

        @SuppressWarnings("unchecked")
        Map<String, String> erreurs = (Map<String, String>) response.getBody().get("erreurs");
        assertThat(erreurs).hasSize(2);
        assertThat(erreurs).containsKeys("idEquipe", "score");
    }

    @Test
    void gererErreurGenerique_devrait_retourner_500_avec_message_generique() {
        Exception exception = new RuntimeException("Erreur imprévue quelconque avec détails internes");

        ResponseEntity<Map<String, Object>> response = handler.gererErreurGenerique(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("status", 500);
        assertThat(response.getBody()).containsEntry("message", "Une erreur inattendue est survenue");
    }

    @Test
    void gererErreurGenerique_ne_devrait_jamais_exposer_le_message_original_de_lexception() {
        Exception exception = new RuntimeException("Détail technique sensible : mot de passe incorrect en base");

        ResponseEntity<Map<String, Object>> response = handler.gererErreurGenerique(exception);

        assertThat(response.getBody().get("message").toString())
                .doesNotContain("mot de passe")
                .doesNotContain("base");
    }
}