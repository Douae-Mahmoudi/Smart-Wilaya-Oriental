package com.wilaya.affectation_service.client;

import com.wilaya.affectation_service.model.EquipeCandidate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RessourceServiceClient {

    private final RestClient restClient;

    public RessourceServiceClient(@Value("${ressource-service.url}") String ressourceServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(ressourceServiceUrl)
                .build();
    }

    public List<EquipeCandidate> trouverEquipesDisponibles(String competence, String zone) {
        if (competence == null || competence.isBlank()) {
            throw new IllegalArgumentException("La compétence est obligatoire pour chercher des équipes disponibles");
        }
        if (zone == null || zone.isBlank()) {
            throw new IllegalArgumentException("La zone est obligatoire pour chercher des équipes disponibles");
        }

        EquipeDisponibleResponse[] reponses = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/equipes/disponibles")
                        .queryParam("competence", competence)
                        .queryParam("zone", zone)
                        .build())
                .retrieve()
                .body(EquipeDisponibleResponse[].class);

        if (reponses == null) {
            return List.of();
        }

        return List.of(reponses).stream()
                .map(r -> new EquipeCandidate(r.id(), r.distance(), r.chargeActuelle(), r.competenceExacte()))
                .toList();
    }

    private record EquipeDisponibleResponse(
            java.util.UUID id,
            Double distance,
            Integer chargeActuelle,
            Boolean competenceExacte
    ) {
    }
}