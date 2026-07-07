package com.wilaya.utilisateur_service.integration;

import com.wilaya.utilisateur_service.domain.port.in.CreerCompteUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UtilisateurControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreerCompteUseCase creerCompteUseCase;

    @Test
    void devrait_refuser_creation_agent_sans_authentification() throws Exception {
        mockMvc.perform(post("/utilisateurs/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Benali\",\"prenom\":\"Ahmed\",\"email\":\"ahmed@test.com\",\"telephone\":\"0600000000\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devrait_refuser_creation_agent_avec_role_agent() throws Exception {
        mockMvc.perform(post("/utilisateurs/agents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Benali\",\"prenom\":\"Ahmed\",\"email\":\"ahmed@test.com\",\"telephone\":\"0600000000\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devrait_autoriser_creation_agent_avec_role_admin() throws Exception {
        when(creerCompteUseCase.creerCompte(any(), any(), any(), any(), any(), any()))
                .thenReturn(UUID.randomUUID());

        mockMvc.perform(post("/utilisateurs/agents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Benali\",\"prenom\":\"Ahmed\",\"email\":\"ahmed@test.com\",\"telephone\":\"0600000000\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void devrait_autoriser_creation_superviseur_avec_role_admin() throws Exception {
        when(creerCompteUseCase.creerCompte(any(), any(), any(), any(), any(), any()))
                .thenReturn(UUID.randomUUID());

        mockMvc.perform(post("/utilisateurs/superviseurs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Alaoui\",\"prenom\":\"Sara\",\"email\":\"sara@test.com\",\"telephone\":\"0600000001\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void devrait_refuser_creation_admin_sans_role_admin() throws Exception {
        mockMvc.perform(post("/utilisateurs/admins")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Idrissi\",\"prenom\":\"Youssef\",\"email\":\"youssef@test.com\",\"telephone\":\"0600000002\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devrait_refuser_creation_agent_avec_donnees_invalides() throws Exception {
        mockMvc.perform(post("/utilisateurs/agents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"\",\"prenom\":\"Ahmed\",\"email\":\"pas-un-email\",\"telephone\":\"0600000000\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_refuser_acces_a_mon_profil_sans_authentification() throws Exception {
        mockMvc.perform(get("/utilisateurs/moi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devrait_retourner_404_si_profil_absent_pour_un_utilisateur_authentifie() throws Exception {
        mockMvc.perform(get("/utilisateurs/moi")
                        .with(jwt().jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound());
    }
}



































