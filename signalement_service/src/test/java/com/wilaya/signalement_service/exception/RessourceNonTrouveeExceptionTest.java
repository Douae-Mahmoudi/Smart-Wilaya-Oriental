package com.wilaya.signalement_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RessourceNonTrouveeExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "La ressource demandée est introuvable";
        RessourceNonTrouveeException exception = new RessourceNonTrouveeException(message);

        assertEquals(message, exception.getMessage());
    }
}