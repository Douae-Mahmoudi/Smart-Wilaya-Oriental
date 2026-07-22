package com.wilaya.affectation_service.client;

import com.wilaya.affectation_service.model.EquipeCandidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RessourceServiceClient.class)
class RessourceServiceClientTest {

    @Autowired
    private RessourceServiceClient client;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server.reset();
    }


    @Test
    void trouverEquipesDisponibles_devraitLancerException_quandCompetenceVide() {
        assertThatThrownBy(() -> client.trouverEquipesDisponibles("", "Oujda"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La compétence est obligatoire pour chercher des équipes disponibles");
    }

    @Test
    void trouverEquipesDisponibles_devraitLancerException_quandZoneVide() {
        assertThatThrownBy(() -> client.trouverEquipesDisponibles("Java", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La zone est obligatoire pour chercher des équipes disponibles");
    }
}