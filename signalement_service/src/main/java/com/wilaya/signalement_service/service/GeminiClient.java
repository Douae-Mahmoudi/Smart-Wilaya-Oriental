package com.wilaya.signalement_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


@Component
public class GeminiClient implements GenerateurIAClient {

    private final RestClient restClient;
    private final String apiKey;

    public GeminiClient(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    @Override
    public String generer(String prompt) {
        GeminiRequest requete = new GeminiRequest(
                List.of(new GeminiRequest.Content(List.of(new GeminiRequest.Part(prompt)))),
                new GeminiRequest.GenerationConfig(new GeminiRequest.ThinkingConfig("low"))
        );

        GeminiResponse reponse = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/gemini-3.6-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .body(requete)
                .retrieve()
                .body(GeminiResponse.class);

        if (reponse == null || reponse.candidates() == null || reponse.candidates().isEmpty()) {
            throw new IllegalStateException("Réponse vide de l'API Gemini");
        }

        return reponse.candidates().get(0).content().parts().get(0).text();
    }

    private record GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
        record Content(List<Part> parts) {}
        record Part(String text) {}
        record GenerationConfig(ThinkingConfig thinkingConfig) {}
        record ThinkingConfig(String thinkingLevel) {}
    }

    private record GeminiResponse(List<Candidate> candidates) {
        record Candidate(Content content) {}
        record Content(List<Part> parts) {}
        record Part(String text) {}
    }
}