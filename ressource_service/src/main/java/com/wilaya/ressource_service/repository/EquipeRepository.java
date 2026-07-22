package com.wilaya.ressource_service.repository;

import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import com.wilaya.ressource_service.model.StatutEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EquipeRepository extends JpaRepository<Equipe, UUID> {

    @Query("SELECT DISTINCT e FROM Equipe e JOIN FETCH e.competences c " +
            "WHERE c = :competence AND e.zoneCouverture = :zone AND e.statut = :statut")
    List<Equipe> findDisponiblesParCompetenceEtZone(
            @Param("competence") CategorieIntervention competence,
            @Param("zone") String zone,
            @Param("statut") StatutEquipe statut);
}