package com.wilaya.ressource_service.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RessourceNonTrouveeExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Resource not found";
        RessourceNonTrouveeException exception = new RessourceNonTrouveeException(message);
        assertInstanceOf(RuntimeException.class, exception);
        assertEquals(message, exception.getMessage());
    }
}