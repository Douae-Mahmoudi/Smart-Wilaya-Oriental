package com.wilaya.ressource_service.integration;

import com.wilaya.ressource_service.config.SecurityConfig;
import com.wilaya.ressource_service.controller.MaterielController;
import com.wilaya.ressource_service.dto.CreerMaterielRequest;
import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.model.StatutMateriel;
import com.wilaya.ressource_service.service.MaterielService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MaterielController.class)
@Import(SecurityConfig.class)
class MaterielControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterielService materielService;

    @Test
    void devrait_refuser_ajout_sans_authentification() throws Exception {
        mockMvc.perform(post("/materiels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"Camion citerne\",\"idEquipeAssociee\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devrait_refuser_ajout_sans_role_admin() throws Exception {
        mockMvc.perform(post("/materiels")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"Camion citerne\",\"idEquipeAssociee\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devrait_autoriser_ajout_avec_role_admin() throws Exception {
        UUID idEquipe = UUID.randomUUID();
        Materiel materiel = new Materiel("Camion citerne", idEquipe);
        when(materielService.ajouterMateriel(any(CreerMaterielRequest.class))).thenReturn(materiel);

        mockMvc.perform(post("/materiels")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"Camion citerne\",\"idEquipeAssociee\":\"" + idEquipe + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("Camion citerne"));
    }

    @Test
    void devrait_refuser_ajout_avec_type_vide() throws Exception {
        mockMvc.perform(post("/materiels")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"\",\"idEquipeAssociee\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_refuser_ajout_avec_id_equipe_manquant() throws Exception {
        mockMvc.perform(post("/materiels")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"Camion citerne\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_autoriser_changement_statut_avec_role_admin() throws Exception {
        UUID id = UUID.randomUUID();
        Materiel materiel = new Materiel("Camion citerne", UUID.randomUUID());
        when(materielService.changerStatut(eq(id), eq(StatutMateriel.EN_MAINTENANCE))).thenReturn(materiel);

        mockMvc.perform(patch("/materiels/{id}/statut", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"EN_MAINTENANCE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void devrait_refuser_changement_statut_sans_authentification() throws Exception {
        mockMvc.perform(patch("/materiels/{id}/statut", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"EN_MAINTENANCE\"}"))
                .andExpect(status().isUnauthorized());
    }
}





















