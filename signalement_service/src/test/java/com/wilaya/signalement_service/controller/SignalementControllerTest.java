package com.wilaya.signalement_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilaya.signalement_service.dto.*;
import com.wilaya.signalement_service.model.*;
import com.wilaya.signalement_service.service.SignalementService;
import com.wilaya.signalement_service.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignalementController.class)
class SignalementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignalementService signalementService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreerSignalement() throws Exception {
        // CORRECTION : Fournissez une adresse non vide ("123 Rue Test")
        CreerSignalementRequest request = new CreerSignalementRequest(
                "AB123456",
                TypeIntervention.VOIRIE,
                "Desc",
                "Zone",
                "123 Rue Test"
        );

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        Signalement s = Mockito.mock(Signalement.class);
        when(signalementService.creerSignalement(any(), any())).thenReturn(s);

        mockMvc.perform(multipart("/signalements")
                        .file(data)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void testChangerStatutSucces() throws Exception {
        UUID id = UUID.randomUUID();
        ChangerStatutRequest request = new ChangerStatutRequest("RESOLU");

        Signalement s = Mockito.mock(Signalement.class);
        when(signalementService.changerStatut(any(), any())).thenReturn(s);

        mockMvc.perform(patch("/signalements/" + id + "/statut")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testConsulterParNumeroSuivi() throws Exception {
        when(signalementService.trouverParNumeroSuivi("NUM123")).thenReturn(Mockito.mock(Signalement.class));

        mockMvc.perform(get("/signalements/NUM123"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    void testListerSignalements() throws Exception {
        mockMvc.perform(get("/signalements"))
                .andExpect(status().isOk());
    }
}