package com.wilaya.signalement_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testGererNonTrouve() {
        RessourceNonTrouveeException ex = new RessourceNonTrouveeException("Non trouvé");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.gererNonTrouve(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Non trouvé", response.getBody().get("message"));
    }

    @Test
    void testGererDoublon() {
        DoublonSignalementException ex = new DoublonSignalementException("Doublon");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.gererDoublon(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Doublon", response.getBody().get("message"));
    }

    @Test
    void testGererArgumentInvalide() {
        IllegalArgumentException ex = new IllegalArgumentException("Argument invalide");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.gererArgumentInvalide(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Argument invalide", response.getBody().get("message"));
    }

    @Test
    void testGererGenerique() {
        Exception ex = new Exception("Erreur fatale");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.gererGenerique(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Une erreur inattendue est survenue", response.getBody().get("message"));
    }
}