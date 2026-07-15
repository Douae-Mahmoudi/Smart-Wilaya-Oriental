package com.wilaya.signalement_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoublonSignalementExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Erreur de doublon détectée";
        DoublonSignalementException exception = new DoublonSignalementException(message);

        assertEquals(message, exception.getMessage());
    }
}