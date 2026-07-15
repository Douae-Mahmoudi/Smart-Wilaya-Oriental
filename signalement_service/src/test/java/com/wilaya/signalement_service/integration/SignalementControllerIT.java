package com.wilaya.signalement_service.integration;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import com.wilaya.signalement_service.service.SignalementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9999/realms/test"
})
class SignalementControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignalementService signalementService;

    private final MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

    private Signalement signalementExemple() {
        return new Signalement("AB123456", TypeIntervention.EAU, "Fuite", "photo.jpg", "Zone Nord", NiveauGravite.MOYENNE);
    }

    @Test
    void devrait_creer_un_signalement_sans_authentification() throws Exception {
        when(signalementService.creerSignalement(any(), any())).thenReturn(signalementExemple());

        mockMvc.perform(multipart("/signalements")
                        .file(photo)
                        .param("cinDeclarant", "AB123456")
                        .param("type", "EAU")
                        .param("description", "Fuite importante")
                        .param("zone", "Zone Nord"))
                .andExpect(status().isCreated());
    }

    @Test
    void devrait_consulter_un_signalement_par_numero_suivi_sans_authentification() throws Exception {
        Signalement signalement = signalementExemple();
        when(signalementService.trouverParNumeroSuivi(signalement.getNumeroSuivi())).thenReturn(signalement);

        mockMvc.perform(get("/signalements/{numeroSuivi}", signalement.getNumeroSuivi()))
                .andExpect(status().isOk());
    }

    @Test
    void devrait_retourner_404_si_numero_suivi_inconnu() throws Exception {
        when(signalementService.trouverParNumeroSuivi("SIG-INCONNU"))
                .thenThrow(new com.wilaya.signalement_service.exception.RessourceNonTrouveeException("introuvable"));

        mockMvc.perform(get("/signalements/{numeroSuivi}", "SIG-INCONNU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void devrait_refuser_liste_sans_authentification() throws Exception {
        mockMvc.perform(get("/signalements"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devrait_refuser_liste_sans_role_superviseur() throws Exception {
        mockMvc.perform(get("/signalements")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void devrait_autoriser_liste_avec_role_superviseur() throws Exception {
        when(signalementService.listerTout()).thenReturn(List.of(signalementExemple()));

        mockMvc.perform(get("/signalements")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR"))))
                .andExpect(status().isOk());
    }

    @Test
    void devrait_refuser_changement_statut_sans_authentification() throws Exception {
        mockMvc.perform(patch("/signalements/{id}/statut", UUID.randomUUID())
                        .contentType("application/json")
                        .content("{\"statut\":\"CLASSIFIE\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devrait_autoriser_changement_statut_avec_role_agent() throws Exception {
        Signalement signalement = signalementExemple();
        when(signalementService.changerStatut(any(UUID.class), any(StatutSignalement.class)))
                .thenReturn(signalement);

        mockMvc.perform(patch("/signalements/{id}/statut", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AGENT")))
                        .contentType("application/json")
                        .content("{\"statut\":\"CLASSIFIE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void devrait_refuser_changement_statut_avec_role_citoyen() throws Exception {
        mockMvc.perform(patch("/signalements/{id}/statut", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CITOYEN")))
                        .contentType("application/json")
                        .content("{\"statut\":\"CLASSIFIE\"}"))
                .andExpect(status().isForbidden());
    }
}