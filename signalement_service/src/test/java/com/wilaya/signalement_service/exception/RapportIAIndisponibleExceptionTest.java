package com.wilaya.signalement_service.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RapportIAIndisponibleExceptionTest {

    @Test
    void devrait_exposer_le_message_et_la_cause() {
        Throwable cause = new RuntimeException("Timeout Gemini API");

        RapportIAIndisponibleException exception =
                new RapportIAIndisponibleException("Le rapport IA est indisponible", cause);

        assertThat(exception.getMessage()).isEqualTo("Le rapport IA est indisponible");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void devrait_etre_une_RuntimeException() {
        RapportIAIndisponibleException exception =
                new RapportIAIndisponibleException("Erreur", new RuntimeException());

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void devrait_pouvoir_etre_levee_et_interceptee() {
        Throwable cause = new IllegalStateException("Connexion refusée");

        assertThatThrownBy(() -> {
            throw new RapportIAIndisponibleException("Service Gemini injoignable", cause);
        })
                .isInstanceOf(RapportIAIndisponibleException.class)
                .hasMessage("Service Gemini injoignable")
                .hasCause(cause);
    }

    @Test
    void devrait_accepter_une_cause_null() {
        RapportIAIndisponibleException exception =
                new RapportIAIndisponibleException("Erreur sans cause connue", null);

        assertThat(exception.getMessage()).isEqualTo("Erreur sans cause connue");
        assertThat(exception.getCause()).isNull();
    }
}






































