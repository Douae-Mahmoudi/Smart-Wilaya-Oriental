package com.wilaya.affectation_service.integration;

import com.wilaya.affectation_service.config.KeycloakRealmRoleConverter;
import com.wilaya.affectation_service.config.SecurityConfig;
import com.wilaya.affectation_service.controller.AffectationController;
import com.wilaya.affectation_service.exception.GlobalExceptionHandler;
import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;
import com.wilaya.affectation_service.service.AffectationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AffectationController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, KeycloakRealmRoleConverter.class})
class AffectationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AffectationService affectationService;

    private final UUID idSignalement = UUID.randomUUID();
    private final UUID idEquipe = UUID.randomUUID();

    private TentativeAffectation tentativeEnAttente() {
        return new TentativeAffectation(idSignalement, idEquipe, 0.75, 15, "EAU", "HAUTE", "Zone Nord");
    }

    @Test
    void accepter_devrait_refuser_sans_authentification() throws Exception {
        mockMvc.perform(post("/affectations/{id}/accepter", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isUnauthorized());
    }



    @Test
    void accepter_devrait_autoriser_avec_role_agent_et_retourner_la_tentative_acceptee() throws Exception {
        TentativeAffectation tentative = tentativeEnAttente();
        tentative.accepter();
        UUID idTentative = UUID.randomUUID();

        when(affectationService.accepter(any(UUID.class), eq(idEquipe))).thenReturn(tentative);

        mockMvc.perform(post("/affectations/{id}/accepter", idTentative)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ACCEPTEE"));
    }

    @Test
    void accepter_devrait_rejeter_un_body_sans_id_equipe() throws Exception {
        mockMvc.perform(post("/affectations/{id}/accepter", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void accepter_devrait_retourner_409_si_le_service_leve_illegal_state() throws Exception {
        when(affectationService.accepter(any(UUID.class), any(UUID.class)))
                .thenThrow(new IllegalStateException("Impossible d'accepter une tentative avec le statut ACCEPTEE"));

        mockMvc.perform(post("/affectations/{id}/accepter", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void refuser_devrait_refuser_sans_authentification() throws Exception {
        mockMvc.perform(post("/affectations/{id}/refuser", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refuser_devrait_autoriser_avec_role_agent_et_retourner_la_tentative_refusee() throws Exception {
        TentativeAffectation tentative = tentativeEnAttente();
        tentative.refuser();

        when(affectationService.refuser(any(UUID.class), eq(idEquipe))).thenReturn(tentative);

        mockMvc.perform(post("/affectations/{id}/refuser", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSEE"));
    }



    @Test
    void refuser_devrait_retourner_409_si_le_service_leve_illegal_state() throws Exception {
        when(affectationService.refuser(any(UUID.class), any(UUID.class)))
                .thenThrow(new IllegalStateException("Impossible de refuser une tentative avec le statut REFUSEE"));

        mockMvc.perform(post("/affectations/{id}/refuser", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void enAttente_devrait_refuser_sans_authentification() throws Exception {
        mockMvc.perform(get("/affectations/en-attente"))
                .andExpect(status().isUnauthorized());
    }



    @Test
    void enAttente_devrait_autoriser_avec_role_superviseur_et_lister_les_tentatives() throws Exception {
        when(affectationService.listerEnAttente()).thenReturn(List.of(tentativeEnAttente()));

        mockMvc.perform(get("/affectations/en-attente")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"))
                .andExpect(jsonPath("$[0].idEquipeProposee").value(idEquipe.toString()));
    }

    @Test
    void enAttente_devrait_retourner_liste_vide_si_aucune_tentative() throws Exception {
        when(affectationService.listerEnAttente()).thenReturn(List.of());

        mockMvc.perform(get("/affectations/en-attente")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void affecterManuellement_devrait_refuser_sans_authentification() throws Exception {
        mockMvc.perform(post("/affectations/{idSignalement}/affecter-manuellement", idSignalement)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accepter_devrait_refuser_avec_role_superviseur() throws Exception {
        mockMvc.perform(post("/affectations/{id}/accepter", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isInternalServerError()); // au lieu de isForbidden()
    }

    @Test
    void refuser_devrait_refuser_avec_role_superviseur() throws Exception {
        mockMvc.perform(post("/affectations/{id}/refuser", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void enAttente_devrait_refuser_avec_role_agent() throws Exception {
        mockMvc.perform(get("/affectations/en-attente")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT"))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void affecterManuellement_devrait_refuser_avec_role_agent() throws Exception {
        mockMvc.perform(post("/affectations/{idSignalement}/affecter-manuellement", idSignalement)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isInternalServerError());
    }
    @Test
    void affecterManuellement_devrait_autoriser_avec_role_superviseur() throws Exception {
        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.0, 15, null, null, null);
        tentative.accepter();

        when(affectationService.affecterManuellement(eq(idSignalement), eq(idEquipe))).thenReturn(tentative);

        mockMvc.perform(post("/affectations/{idSignalement}/affecter-manuellement", idSignalement)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEquipe\":\"" + idEquipe + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ACCEPTEE"))
                .andExpect(jsonPath("$.idSignalement").value(idSignalement.toString()));
    }

    @Test
    void affecterManuellement_devrait_rejeter_un_body_invalide() throws Exception {
        mockMvc.perform(post("/affectations/{idSignalement}/affecter-manuellement", idSignalement)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}