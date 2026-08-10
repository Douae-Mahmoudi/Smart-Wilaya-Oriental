package com.wilaya.utilisateur_service.api.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EquipeAgentResponseTest {

    @Test
    void equipeAgentResponse_doitCreerRecordAvecIdEquipe() {
        UUID id = UUID.randomUUID();
        EquipeAgentResponse response = new EquipeAgentResponse(id);

        assertThat(response.idEquipe()).isEqualTo(id);
    }
}