package com.wilaya.signalement_service.config;

import com.wilaya.signalement_service.exception.RessourceNonTrouveeException;
import com.wilaya.signalement_service.service.GeminiClient;
import com.wilaya.signalement_service.service.SignalementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestSecurityConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignalementService signalementService;

    @MockBean
    private GeminiClient geminiClient;

    @Test
    void shouldPermitPublicEndpoints() throws Exception {
        when(signalementService.trouverParNumeroSuivi("123"))
                .thenThrow(new RessourceNonTrouveeException("Aucun signalement trouve pour le numero de suivi 123"));

        mockMvc.perform(get("/signalements/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDenyAccessToProtectedEndpointsWithoutAuth() throws Exception {
        mockMvc.perform(get("/signalements"))
                .andExpect(status().isUnauthorized());
    }
}