package com.wilaya.signalement_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;

public interface SignalementRepository extends JpaRepository<Signalement, UUID> {

    Optional<Signalement> findByNumeroSuivi(String numeroSuivi);

    List<Signalement> findByType(TypeIntervention type);

    List<Signalement> findByStatut(StatutSignalement statut);

    List<Signalement> findByZone(String zone);

    // Méthode pour détecter les doublons par type + zone + adresse (nouvelle)
    List<Signalement> findByTypeAndZoneAndAdresseAndDateCreationAfterAndStatutNotIn(
            TypeIntervention type,
            String zone,
            String adresse,
            LocalDateTime seuil,
            List<StatutSignalement> statutsExclus
    );

    // Méthode pour détecter les doublons par type + zone (ancienne, conservée pour compatibilité)
    List<Signalement> findByTypeAndZoneAndDateCreationAfterAndStatutNotIn(
            TypeIntervention type,
            String zone,
            LocalDateTime seuil,
            List<StatutSignalement> statutsExclus
    );

    // Méthode pour détecter les doublons par CIN
    List<Signalement> findByCinDeclarantAndDateCreationAfterAndStatutNotIn(
            String cinDeclarant,
            LocalDateTime seuil,
            List<StatutSignalement> statutsExclus
    );
}