package com.wilaya.ressource_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilaya.ressource_service.dto.ChangerStatutRequest;
import com.wilaya.ressource_service.dto.CreerMaterielRequest;
import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.model.StatutMateriel;
import com.wilaya.ressource_service.service.MaterielService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MaterielController.class)
@AutoConfigureMockMvc(addFilters = false)
class MaterielControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaterielService materielService;

    @Test
    void ajouterMateriel_devrait_retourner_201_avec_le_materiel_cree() throws Exception {
        UUID idEquipe = UUID.randomUUID();
        CreerMaterielRequest request = new CreerMaterielRequest("Pelle", idEquipe);
        Materiel materielCree = new Materiel("Pelle", idEquipe);

        when(materielService.ajouterMateriel(any(CreerMaterielRequest.class))).thenReturn(materielCree);

        mockMvc.perform(post("/materiels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("Pelle"))
                .andExpect(jsonPath("$.idEquipeAssociee").value(idEquipe.toString()))
                .andExpect(jsonPath("$.statut").value("DISPONIBLE"));

        verify(materielService).ajouterMateriel(any(CreerMaterielRequest.class));
    }

    @Test
    void ajouterMateriel_devrait_retourner_400_si_type_manquant() throws Exception {
        UUID idEquipe = UUID.randomUUID();
        String jsonInvalide = String.format("""
                {
                    "idEquipeAssociee": "%s"
                }
                """, idEquipe);

        mockMvc.perform(post("/materiels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ajouterMateriel_devrait_retourner_400_si_idEquipeAssociee_manquant() throws Exception {
        String jsonInvalide = """
                {
                    "type": "Pelle"
                }
                """;

        mockMvc.perform(post("/materiels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changerStatut_devrait_retourner_200_avec_le_materiel_mis_a_jour() throws Exception {
        UUID idMateriel = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        ChangerStatutRequest request = new ChangerStatutRequest("EN_MAINTENANCE");

        Materiel materielMisAJour = new Materiel("Pelle", idEquipe);
        materielMisAJour.changerStatut(StatutMateriel.EN_MAINTENANCE);

        when(materielService.changerStatut(eq(idMateriel), eq(StatutMateriel.EN_MAINTENANCE)))
                .thenReturn(materielMisAJour);

        mockMvc.perform(patch("/materiels/{id}/statut", idMateriel)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_MAINTENANCE"));

        verify(materielService).changerStatut(idMateriel, StatutMateriel.EN_MAINTENANCE);
    }

    @Test
    void changerStatut_devrait_retourner_400_si_statut_manquant() throws Exception {
        UUID idMateriel = UUID.randomUUID();
        String jsonInvalide = "{}";

        mockMvc.perform(patch("/materiels/{id}/statut", idMateriel)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changerStatut_devrait_retourner_404_si_statut_invalide() throws Exception {
        UUID idMateriel = UUID.randomUUID();
        String jsonInvalide = """
                {
                    "statut": "STATUT_INEXISTANT"
                }
                """;

        mockMvc.perform(patch("/materiels/{id}/statut", idMateriel)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide))
                .andExpect(status().isNotFound());
    }
}