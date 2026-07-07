package com.wilaya.utilisateur_service.integration;

import com.wilaya.utilisateur_service.domain.port.out.EmailSenderPort;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ReinitialisationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentiteProviderPort identiteProviderPort;

    @MockBean
    private EmailSenderPort emailSenderPort;

    @Test
    void devrait_repondre_200_meme_si_email_inconnu() throws Exception {
        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"inconnu@test.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void devrait_rejeter_un_email_invalide() throws Exception {
        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"pas-un-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_rejeter_un_body_vide() throws Exception {
        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_rejeter_une_verification_avec_code_invalide() throws Exception {
        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"inconnu@test.com\",\"code\":\"000000\",\"nouveauMotDePasse\":\"NouveauMdp123!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_rejeter_une_verification_avec_code_trop_court() throws Exception {
        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"inconnu@test.com\",\"code\":\"123\",\"nouveauMotDePasse\":\"NouveauMdp123!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devrait_rejeter_une_verification_avec_mot_de_passe_trop_court() throws Exception {
        mockMvc.perform(post("/utilisateurs/mot-de-passe-oublie/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"inconnu@test.com\",\"code\":\"123456\",\"nouveauMotDePasse\":\"abc\"}"))
                .andExpect(status().isBadRequest());
    }
}





























