package com.wilaya.ressource_service.integration;

import com.wilaya.ressource_service.config.KeycloakRealmRoleConverter;
import com.wilaya.ressource_service.config.SecurityConfig;
import com.wilaya.ressource_service.controller.EquipeController;
import com.wilaya.ressource_service.dto.CreerEquipeRequest;
import com.wilaya.ressource_service.dto.EquipeDisponibleResponse;
import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import com.wilaya.ressource_service.model.StatutEquipe;
import com.wilaya.ressource_service.service.EquipeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EquipeController.class)
@Import(SecurityConfig.class)
class EquipeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipeService equipeService;

    @MockBean
    private KeycloakRealmRoleConverter keycloakRealmRoleConverter;

    @Test
    void devrait_refuser_creation_sans_authentification() throws Exception {
        mockMvc.perform(post("/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Equipe Eau\",\"competences\":[\"EAU\"],\"zoneCouverture\":\"Zone Nord\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devrait_refuser_creation_sans_role_admin() throws Exception {
        mockMvc.perform(post("/equipes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Equipe Eau\",\"competences\":[\"EAU\"],\"zoneCouverture\":\"Zone Nord\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devrait_autoriser_creation_avec_role_admin() throws Exception {
        Equipe equipe = new Equipe("Equipe Eau", List.of(CategorieIntervention.EAU), "Zone Nord");
        when(equipeService.creerEquipe(any(CreerEquipeRequest.class))).thenReturn(equipe);

        mockMvc.perform(post("/equipes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Equipe Eau\",\"competences\":[\"EAU\"],\"zoneCouverture\":\"Zone Nord\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Equipe Eau"));
    }

    @Test
    void devrait_refuser_creation_avec_nom_vide() throws Exception {
        mockMvc.perform(post("/equipes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"\",\"competences\":[\"EAU\"],\"zoneCouverture\":\"Zone Nord\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_lister_les_equipes_disponibles_sans_authentification() throws Exception {
        EquipeDisponibleResponse response = new EquipeDisponibleResponse(
                UUID.randomUUID(), "Equipe Eau", List.of(CategorieIntervention.EAU), "Zone Nord", true);
        when(equipeService.trouverDisponibles(eq(CategorieIntervention.EAU), eq("Zone Nord")))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/equipes/disponibles")
                        .param("competence", "EAU")
                        .param("zone", "Zone Nord"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Equipe Eau"))
                .andExpect(jsonPath("$[0].materielDisponible").value(true));
    }

    @Test
    void devrait_refuser_changement_statut_sans_authentification() throws Exception {
        mockMvc.perform(patch("/equipes/{id}/statut", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"EN_INTERVENTION\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devrait_autoriser_changement_statut_avec_role_admin() throws Exception {
        UUID id = UUID.randomUUID();
        Equipe equipe = new Equipe("Equipe Eau", List.of(CategorieIntervention.EAU), "Zone Nord");
        when(equipeService.changerStatut(eq(id), eq(StatutEquipe.EN_INTERVENTION))).thenReturn(equipe);

        mockMvc.perform(patch("/equipes/{id}/statut", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"EN_INTERVENTION\"}"))
                .andExpect(status().isOk());
    }
}
