package com.wilaya.signalement_service.repository;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SignalementRepository extends JpaRepository<Signalement, UUID> {

    Optional<Signalement> findByNumeroSuivi(String numeroSuivi);

    List<Signalement> findByType(TypeIntervention type);

    List<Signalement> findByStatut(StatutSignalement statut);

    List<Signalement> findByZone(String zone);

    List<Signalement> findByTypeAndZoneAndAdresseAndDateCreationAfterAndStatutNotIn(
            TypeIntervention type,
            String zone,
            String adresse,
            LocalDateTime seuil,
            List<StatutSignalement> statutsExclus
    );

    List<Signalement> findByTypeAndZoneAndDateCreationAfterAndStatutNotIn(
            TypeIntervention type,
            String zone,
            LocalDateTime seuil,
            List<StatutSignalement> statutsExclus
    );

    List<Signalement> findByCinDeclarantAndDateCreationAfterAndStatutNotIn(
            String cinDeclarant,
            LocalDateTime seuil,
            List<StatutSignalement> statutsExclus
    );


    List<Signalement> findByTypeAndStatutNotIn(TypeIntervention type, List<StatutSignalement> statutsExclus);


    List<Signalement> findByTypeAndStatut(TypeIntervention type, StatutSignalement statut);
}



















































































