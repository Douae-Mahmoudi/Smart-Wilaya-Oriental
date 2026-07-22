package com.wilaya.affectation_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeycloakRealmRoleConverter keycloakRealmRoleConverter;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void swaggerEndpoints_devraientEtreAccessibleSansAuthentification() throws Exception {
        // Teste l'accès public au chemin Swagger configuré dans votre SecurityConfig
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Soit Swagger répond (200), soit il redirige/renvoie vers une ressource valide,
                    // l'important est qu'il ne soit pas bloqué par une erreur 401/403 d'authentification.
                    org.junit.jupiter.api.Assertions.assertNotEquals(401, status);
                    org.junit.jupiter.api.Assertions.assertNotEquals(403, status);
                });
    }

    @Test
    void anyRequest_devraitEtreRejete_quandNonAuthentifie() throws Exception {
        // Un endpoint protégé de l'application doit rejeter l'accès anonyme
        mockMvc.perform(get("/api/secured-endpoint"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertTrue(
                            status == 401 || status == 403,
                            "L'endpoint sécurisé devrait rejeter l'accès non authentifié, reçu : " + status
                    );
                });
    }

    @Test
    @WithMockUser
    void anyRequest_devraitEtreAccepte_quandAuthentifie() throws Exception {
        // Avec un utilisateur mocké, l'accès ne doit pas être bloqué par la sécurité (pas de 401/403)
        mockMvc.perform(get("/api/secured-endpoint"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertTrue(
                            status != 401 && status != 403,
                            "L'utilisateur authentifié ne devrait pas recevoir 401 ou 403, reçu : " + status
                    );
                });
    }
}



































































