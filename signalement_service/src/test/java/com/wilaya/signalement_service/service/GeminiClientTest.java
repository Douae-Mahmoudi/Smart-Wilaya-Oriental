package com.wilaya.signalement_service.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiClientTest {

    private GeminiClient geminiClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() throws Exception {
        geminiClient = new GeminiClient("fake-api-key");


        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClientDeTest = builder.build();

        Field champRestClient = GeminiClient.class.getDeclaredField("restClient");
        champRestClient.setAccessible(true);
        champRestClient.set(geminiClient, restClientDeTest);
    }

    @Test
    void devrait_retourner_le_texte_genere_par_gemini() {
        String jsonReponse = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          { "text": "Voici le rapport généré." }
                        ]
                      }
                    }
                  ]
                }
                """;

        mockServer.expect(requestTo(Matchers.containsString("/models/gemini-3.6-flash:generateContent")))
                .andExpect(requestTo(Matchers.containsString("key=fake-api-key")))
                .andExpect(method(POST))
                .andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

        String resultat = geminiClient.generer("Résume ce signalement");

        assertThat(resultat).isEqualTo("Voici le rapport généré.");
        mockServer.verify();
    }

    @Test
    void devrait_lever_exception_si_liste_de_candidats_vide() {
        String jsonReponse = "{ \"candidates\": [] }";

        mockServer.expect(requestTo(Matchers.containsString("/models/gemini-3.6-flash:generateContent")))
                .andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> geminiClient.generer("Prompt test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("vide");
    }

    @Test
    void devrait_lever_exception_si_champ_candidats_absent() {
        String jsonReponse = "{}";

        mockServer.expect(requestTo(Matchers.containsString("/models/gemini-3.6-flash:generateContent")))
                .andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> geminiClient.generer("Prompt test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("vide");
    }

    @Test
    void devrait_lever_exception_si_reponse_null() {
        mockServer.expect(requestTo(Matchers.containsString("/models/gemini-3.6-flash:generateContent")))
                .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> geminiClient.generer("Prompt test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("vide");
    }
}
