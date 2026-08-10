package com.wilaya.utilisateur_service.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void gererIllegalArgumentException_doitRetournerBadRequestAvecMessage() {
        String message = "Invalid argument";
        IllegalArgumentException ex = new IllegalArgumentException(message);

        ResponseEntity<String> response = handler.gererIllegalArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(message);
    }

    @Test
    void gererIllegalStateException_doitRetournerConflictAvecMessage() {
        String message = "State conflict";
        IllegalStateException ex = new IllegalStateException(message);

        ResponseEntity<String> response = handler.gererIllegalStateException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo(message);
    }
}