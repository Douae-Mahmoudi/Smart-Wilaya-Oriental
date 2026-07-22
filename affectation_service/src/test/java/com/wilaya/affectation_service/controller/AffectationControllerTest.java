package com.wilaya.affectation_service.controller;

import com.wilaya.affectation_service.dto.AccepterAffectationRequest;
import com.wilaya.affectation_service.dto.AffectationResponse;
import com.wilaya.affectation_service.model.TentativeAffectation;
import com.wilaya.affectation_service.service.AffectationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AffectationController.class)
class AffectationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AffectationService affectationService;

    @Test
    @WithMockUser(roles = "AGENT")
    void accepter_devraitRetournerAffectationReponse_quandRequeteValide() throws Exception {
        UUID id = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        AccepterAffectationRequest request = new AccepterAffectationRequest(idEquipe);

        // Utilisation du constructeur public pour éviter l'erreur d'accès protégé
        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), idEquipe, 1.0, 30, "Voirie", "CRITIQUE", "Zone Nord"
        );

        when(affectationService.accepter(eq(id), eq(idEquipe))).thenReturn(tentative);

        mockMvc.perform(post("/affectations/{id}/accepter", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void refuser_devraitRetournerAffectationReponse_quandRequeteValide() throws Exception {
        UUID id = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        AccepterAffectationRequest request = new AccepterAffectationRequest(idEquipe);

        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), idEquipe, 1.0, 30, "Voirie", "CRITIQUE", "Zone Nord"
        );

        when(affectationService.refuser(eq(id), eq(idEquipe))).thenReturn(tentative);

        mockMvc.perform(post("/affectations/{id}/refuser", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    void enAttente_devraitRetournerListeAffectations() throws Exception {
        when(affectationService.listerEnAttente()).thenReturn(List.of());

        mockMvc.perform(get("/affectations/en-attente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    void affecterManuellement_devraitRetournerAffectationReponse_quandRequeteValide() throws Exception {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        AccepterAffectationRequest request = new AccepterAffectationRequest(idEquipe);

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 1.0, 30, "Voirie", "CRITIQUE", "Zone Nord"
        );

        when(affectationService.affecterManuellement(eq(idSignalement), eq(idEquipe))).thenReturn(tentative);

        mockMvc.perform(post("/affectations/{idSignalement}/affecter-manuellement", idSignalement)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}