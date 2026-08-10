package com.wilaya.signalement_service.controller;

import com.wilaya.signalement_service.dto.RapportIAResponse;
import com.wilaya.signalement_service.exception.RapportIAIndisponibleException;
import com.wilaya.signalement_service.service.RapportIAService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RapportIAController.class)
class RapportIAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RapportIAService rapportIAService;

    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    void devrait_renvoyer_200_et_le_rapport_pour_un_superviseur() throws Exception {
        RapportIAResponse reponse = new RapportIAResponse("Activité stable ce mois-ci.", LocalDateTime.now());
        when(rapportIAService.genererRapport()).thenReturn(reponse);

        mockMvc.perform(post("/rapports-ia/generer").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Activité stable ce mois-ci."));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void devrait_renvoyer_200_pour_un_agent() throws Exception {
        mockMvc.perform(post("/rapports-ia/generer").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void devrait_renvoyer_401_ou_403_si_non_authentifie() throws Exception {
        mockMvc.perform(post("/rapports-ia/generer").with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    void devrait_renvoyer_500_avec_message_generique_si_lia_est_indisponible() throws Exception {
        when(rapportIAService.genererRapport())
                .thenThrow(new RapportIAIndisponibleException("Le service IA est actuellement indisponible.", null));

        mockMvc.perform(post("/rapports-ia/generer").with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Une erreur inattendue est survenue"));
    }
}