package com.wilaya.utilisateur_service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilaya.utilisateur_service.api.dto.ChangerMotDePasseRequest;
import com.wilaya.utilisateur_service.api.dto.CreerCompteRequest;
import com.wilaya.utilisateur_service.api.dto.ModifierProfilRequest;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.in.ChangerMotDePasseUseCase;
import com.wilaya.utilisateur_service.domain.port.in.CreerCompteUseCase;
import com.wilaya.utilisateur_service.domain.port.in.ListerAgentsUseCase;
import com.wilaya.utilisateur_service.domain.port.in.ModifierProfilUseCase;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UtilisateurController.class)
class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfilUtilisateurRepository profilRepository;

    @MockBean
    private CreerCompteUseCase creerCompteUseCase;

    @MockBean
    private ModifierProfilUseCase modifierProfilUseCase;

    @MockBean
    private ChangerMotDePasseUseCase changerMotDePasseUseCase;

    @MockBean
    private ListerAgentsUseCase listerAgentsUseCase;

    private final UUID idKeycloak = UUID.randomUUID();


    @Test
    void monProfilRenvoie200AvecLeProfilQuandIlExiste() throws Exception {
        ProfilUtilisateur profil = new ProfilUtilisateur(idKeycloak, "Benali", "Karim", "0600000000", "karim@example.com");
        when(profilRepository.findByIdKeycloak(idKeycloak)).thenReturn(Optional.of(profil));

        mockMvc.perform(get("/utilisateurs/moi")
                        .with(jwt().jwt(j -> j.subject(idKeycloak.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Benali"))
                .andExpect(jsonPath("$.email").value("karim@example.com"));
    }

    @Test
    void monProfilRenvoie404QuandLeProfilNExistePas() throws Exception {
        when(profilRepository.findByIdKeycloak(idKeycloak)).thenReturn(Optional.empty());

        mockMvc.perform(get("/utilisateurs/moi")
                        .with(jwt().jwt(j -> j.subject(idKeycloak.toString()))))
                .andExpect(status().isNotFound());
    }


    @Test
    void creerAgentAvecRequeteValideRenvoie201EtAppelleLeUseCaseAvecRoleAgent() throws Exception {
        var request = new CreerCompteRequest("Benali", "Karim", "karim@example.com", "0600000000", UUID.randomUUID());

        mockMvc.perform(post("/utilisateurs/agents")
                        .with(jwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(creerCompteUseCase).creerCompte(
                eq("Benali"), eq("Karim"), eq("karim@example.com"), eq("0600000000"),
                eq("AGENT"), eq(request.idEquipe())
        );
    }

    @Test
    void creerAgentAvecEmailInvalideRenvoie400() throws Exception {
        var request = new CreerCompteRequest("Benali", "Karim", "pas-un-email", "0600000000", UUID.randomUUID());

        mockMvc.perform(post("/utilisateurs/agents")
                        .with(jwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(creerCompteUseCase);
    }


    @Test
    void creerSuperviseurRenvoie201EtAppelleLeUseCaseAvecRoleSuperviseurEtIdEquipeNull() throws Exception {
        var request = new CreerCompteRequest("Alaoui", "Yassine", "yassine@example.com", "0611111111", UUID.randomUUID());

        mockMvc.perform(post("/utilisateurs/superviseurs")
                        .with(jwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(creerCompteUseCase).creerCompte(
                eq("Alaoui"), eq("Yassine"), eq("yassine@example.com"), eq("0611111111"),
                eq("SUPERVISEUR"), eq((UUID) null)
        );
    }


    @Test
    void creerAdminRenvoie201EtAppelleLeUseCaseAvecRoleAdminEtIdEquipeNull() throws Exception {
        var request = new CreerCompteRequest("Tazi", "Sara", "sara@example.com", "0622222222", null);

        mockMvc.perform(post("/utilisateurs/admins")
                        .with(jwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(creerCompteUseCase).creerCompte(
                eq("Tazi"), eq("Sara"), eq("sara@example.com"), eq("0622222222"),
                eq("ADMIN"), eq((UUID) null)
        );
    }


    @Test
    void modifierMonProfilAvecRequeteValideRenvoie204() throws Exception {
        var request = new ModifierProfilRequest("Alaoui", "Yassine", "0611111111", true);

        mockMvc.perform(patch("/utilisateurs/moi")
                        .with(jwt().jwt(j -> j.subject(idKeycloak.toString())))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(modifierProfilUseCase).modifierProfil(idKeycloak, "Alaoui", "Yassine", "0611111111", true);
    }

    @Test
    void modifierMonProfilAvecNomVideRenvoie400() throws Exception {
        var request = new ModifierProfilRequest("", "Yassine", "0611111111", true);

        mockMvc.perform(patch("/utilisateurs/moi")
                        .with(jwt().jwt(j -> j.subject(idKeycloak.toString())))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(modifierProfilUseCase);
    }


    @Test
    void changerMonMotDePasseAvecRequeteValideRenvoie204() throws Exception {
        var request = new ChangerMotDePasseRequest("ancienMdp1", "nouveauMdp123");

        mockMvc.perform(patch("/utilisateurs/moi/mot-de-passe")
                        .with(jwt().jwt(j -> j.subject(idKeycloak.toString())))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(changerMotDePasseUseCase).changerMotDePasse(idKeycloak, "ancienMdp1", "nouveauMdp123");
    }

    @Test
    void changerMonMotDePasseAvecNouveauMotDePasseTropCourtRenvoie400() throws Exception {
        var request = new ChangerMotDePasseRequest("ancienMdp1", "court1");

        mockMvc.perform(patch("/utilisateurs/moi/mot-de-passe")
                        .with(jwt().jwt(j -> j.subject(idKeycloak.toString())))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(changerMotDePasseUseCase);
    }


    @Test
    void listerAgentsParEquipeRenvoie200AvecLaListeMappee() throws Exception {
        UUID idEquipe = UUID.randomUUID();
        when(listerAgentsUseCase.listerParEquipe(idEquipe)).thenReturn(List.of());

        mockMvc.perform(get("/utilisateurs/agents")
                        .queryParam("equipe", idEquipe.toString())
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(listerAgentsUseCase).listerParEquipe(idEquipe);
    }


}

















