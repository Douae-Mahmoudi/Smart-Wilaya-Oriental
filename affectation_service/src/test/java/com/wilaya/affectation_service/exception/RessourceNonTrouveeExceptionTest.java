package com.wilaya.affectation_service.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RessourceNonTrouveeExceptionTest {

    @Test
    void devraitConserverLeMessage() {
        RessourceNonTrouveeException exception = new RessourceNonTrouveeException("Ressource introuvable");

        assertThat(exception.getMessage()).isEqualTo("Ressource introuvable");
    }

    @Test
    void devraitEtreUneRuntimeException() {
        RessourceNonTrouveeException exception = new RessourceNonTrouveeException("Ressource introuvable");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}











































