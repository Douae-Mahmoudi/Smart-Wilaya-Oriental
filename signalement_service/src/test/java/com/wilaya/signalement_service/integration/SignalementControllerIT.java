package com.wilaya.signalement_service.integration;

import com.wilaya.signalement_service.model.*;
import com.wilaya.signalement_service.service.SignalementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9999/realms/test"})
class SignalementControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private SignalementService signalementService;

    @BeforeEach
    void setup() {
        // Cette configuration force le traitement de la sécurité AVANT le GlobalExceptionHandler
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private Signalement signalementExemple() {
        return new Signalement("AB123456", TypeIntervention.EAU, "Fuite", "p.jpg", "Nord", NiveauGravite.MOYENNE, "Rue Test");
    }

    @Test
    void devrait_creer_un_signalement() throws Exception {
        when(signalementService.creerSignalement(any(), any())).thenReturn(signalementExemple());

        String json = "{\"cinDeclarant\": \"AB123456\", \"type\": \"EAU\", \"description\": \"Fuite\", \"zone\": \"Nord\", \"adresse\": \"Rue Test\"}";
        MockMultipartFile data = new MockMultipartFile("data", "", "application/json", json.getBytes());
        MockMultipartFile photo = new MockMultipartFile("photo", "p.jpg", "image/jpeg", new byte[]{1});

        mockMvc.perform(multipart("/signalements")
                        .file(data)
                        .file(photo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void devrait_autoriser_liste_avec_role_superviseur() throws Exception {
        when(signalementService.listerTout()).thenReturn(List.of(signalementExemple()));

        mockMvc.perform(get("/signalements")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SUPERVISEUR"))))
                .andExpect(status().isOk());
    }


}