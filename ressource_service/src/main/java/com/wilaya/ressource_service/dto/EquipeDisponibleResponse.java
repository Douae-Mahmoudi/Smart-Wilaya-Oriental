package com.wilaya.ressource_service.dto;

import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;

import java.util.List;
import java.util.UUID;

public record EquipeDisponibleResponse(
        UUID id,
        String nom,
        List<CategorieIntervention> competences,
        String zoneCouverture,
        boolean materielDisponible
) {
    public static EquipeDisponibleResponse depuis(Equipe equipe, boolean materielDisponible) {
        return new EquipeDisponibleResponse(
                equipe.getId(), equipe.getNom(), equipe.getCompetences(),
                equipe.getZoneCouverture(), materielDisponible
        );
    }
}