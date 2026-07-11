package com.wilaya.ressource_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilaya.ressource_service.dto.ChangerStatutRequest;
import com.wilaya.ressource_service.dto.CreerEquipeRequest;
import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import com.wilaya.ressource_service.model.StatutEquipe;
import com.wilaya.ressource_service.service.EquipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EquipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EquipeService equipeService;

    @Test
    void creerEquipe_devrait_retourner_201_avec_lequipe_creee() throws Exception {
        CreerEquipeRequest request = new CreerEquipeRequest(
                "Equipe Eau Nord", List.of(CategorieIntervention.EAU), "ZoneNord");

        Equipe equipeCreee = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "ZoneNord");

        when(equipeService.creerEquipe(any(CreerEquipeRequest.class))).thenReturn(equipeCreee);

        mockMvc.perform(post("/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Equipe Eau Nord"))
                .andExpect(jsonPath("$.zoneCouverture").value("ZoneNord"));

        verify(equipeService).creerEquipe(any(CreerEquipeRequest.class));
    }

    @Test
    void creerEquipe_devrait_retourner_400_si_nom_manquant() throws Exception {
        String jsonInvalide = """
                {
                    "nom": "",
                    "competences": ["EAU"],
                    "zoneCouverture": "ZoneNord"
                }
                """;

        mockMvc.perform(post("/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide))
                .andExpect(status().isBadRequest());
    }

    @Test
    void equipesDisponibles_devrait_retourner_200_avec_la_liste() throws Exception {
        when(equipeService.trouverDisponibles(CategorieIntervention.EAU, "ZoneNord"))
                .thenReturn(List.of());

        mockMvc.perform(get("/equipes/disponibles")
                        .param("competence", "EAU")
                        .param("zone", "ZoneNord"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(equipeService).trouverDisponibles(CategorieIntervention.EAU, "ZoneNord");
    }

    @Test
    void equipesDisponibles_devrait_retourner_400_si_competence_manquante() throws Exception {
        mockMvc.perform(get("/equipes/disponibles")
                        .param("zone", "ZoneNord"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Une erreur inattendue est survenue"));
    }

    @Test
    void changerStatut_devrait_retourner_200_avec_lequipe_mise_a_jour() throws Exception {
        UUID idEquipe = UUID.randomUUID();
        ChangerStatutRequest request = new ChangerStatutRequest("EN_INTERVENTION");

        Equipe equipeMiseAJour = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "ZoneNord");
        equipeMiseAJour.changerStatut(StatutEquipe.EN_INTERVENTION);

        when(equipeService.changerStatut(eq(idEquipe), eq(StatutEquipe.EN_INTERVENTION)))
                .thenReturn(equipeMiseAJour);

        mockMvc.perform(patch("/equipes/{id}/statut", idEquipe)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_INTERVENTION"));

        verify(equipeService).changerStatut(idEquipe, StatutEquipe.EN_INTERVENTION);
    }

    @Test
    void changerStatut_devrait_retourner_404_si_statut_invalide() throws Exception {
        UUID idEquipe = UUID.randomUUID();
        ChangerStatutRequest request = new ChangerStatutRequest("VALEUR_INEXISTANTE");

        mockMvc.perform(patch("/equipes/{id}/statut", idEquipe)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}