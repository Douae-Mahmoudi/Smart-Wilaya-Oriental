package com.wilaya.utilisateur_service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilaya.utilisateur_service.api.dto.DemandeResetRequest;
import com.wilaya.utilisateur_service.api.dto.VerifierCodeRequest;
import com.wilaya.utilisateur_service.domain.port.in.DemanderReinitialisationUseCase;
import com.wilaya.utilisateur_service.domain.port.in.VerifierCodeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ReinitialisationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReinitialisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DemanderReinitialisationUseCase demanderReinitialisationUseCase;

    @MockBean
    private VerifierCodeUseCase verifierCodeUseCase;

    private static final String MESSAGE_GENERIQUE =
            "Si cet email existe dans notre système, un code de vérification a été envoyé.";


    @Test
    void demanderAvecEmailValideRenvoie200EtLeMessageGenerique() throws Exception {
        var request = new DemandeResetRequest("karim@example.com");

        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(MESSAGE_GENERIQUE));

        verify(demanderReinitialisationUseCase).demanderReinitialisation("karim@example.com");
    }

    @Test
    void demanderAvecEmailInvalideRenvoie400EtNAppellePasLeUseCase() throws Exception {
        var request = new DemandeResetRequest("pas-un-email");

        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(demanderReinitialisationUseCase);
    }

    @Test
    void demanderRenvoieLeMemeMessageGeneriqueMemeSiLEmailNExistePas() throws Exception {

        var request = new DemandeResetRequest("inconnu@example.com");
        doNothing().when(demanderReinitialisationUseCase).demanderReinitialisation("inconnu@example.com");

        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(MESSAGE_GENERIQUE));
    }


    @Test
    void verifierAvecRequeteValideRenvoie200EtAppelleLeUseCase() throws Exception {
        var request = new VerifierCodeRequest("karim@example.com", "123456", "nouveauMdp123");

        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie/verifier")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe réinitialisé avec succès."));

        verify(verifierCodeUseCase).verifierEtReinitialiser("karim@example.com", "123456", "nouveauMdp123");
    }

    @Test
    void verifierAvecCodeTropCourtRenvoie400EtNAppellePasLeUseCase() throws Exception {
        var request = new VerifierCodeRequest("karim@example.com", "123", "nouveauMdp123");

        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie/verifier")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(verifierCodeUseCase);
    }

    @Test
    void verifierAvecNouveauMotDePasseTropCourtRenvoie400() throws Exception {
        var request = new VerifierCodeRequest("karim@example.com", "123456", "court1");

        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie/verifier")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(verifierCodeUseCase);
    }


}



















