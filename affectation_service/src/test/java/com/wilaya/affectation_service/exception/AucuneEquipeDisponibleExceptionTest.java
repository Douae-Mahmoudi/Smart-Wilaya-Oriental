package com.wilaya.affectation_service.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AucuneEquipeDisponibleExceptionTest {

    @Test
    void devrait_conserver_le_message_fourni() {
        AucuneEquipeDisponibleException exception =
                new AucuneEquipeDisponibleException("Aucune équipe disponible pour le signalement X");

        assertThat(exception.getMessage()).isEqualTo("Aucune équipe disponible pour le signalement X");
    }

    @Test
    void devrait_etre_une_runtime_exception() {
        AucuneEquipeDisponibleException exception =
                new AucuneEquipeDisponibleException("message");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void devrait_pouvoir_etre_levee_et_interceptee() {
        assertThatThrownBy(() -> {
            throw new AucuneEquipeDisponibleException("Test");
        })
                .isInstanceOf(AucuneEquipeDisponibleException.class)
                .hasMessage("Test");
    }
}