package com.wilaya.ressource_service.service;

import com.wilaya.ressource_service.dto.CreerEquipeRequest;
import com.wilaya.ressource_service.dto.EquipeDisponibleResponse;
import com.wilaya.ressource_service.exception.RessourceNonTrouveeException;
import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.model.StatutEquipe;
import com.wilaya.ressource_service.repository.EquipeRepository;
import com.wilaya.ressource_service.repository.MaterielRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final MaterielRepository materielRepository;

    public EquipeService(EquipeRepository equipeRepository, MaterielRepository materielRepository) {
        this.equipeRepository = equipeRepository;
        this.materielRepository = materielRepository;
    }

    public Equipe creerEquipe(CreerEquipeRequest request) {
        Equipe equipe = new Equipe(request.nom(), request.competences(), request.zoneCouverture());
        return equipeRepository.save(equipe);
    }

    public List<EquipeDisponibleResponse> trouverDisponibles(CategorieIntervention competence, String zone) {
        List<Equipe> equipes = equipeRepository.findDisponiblesParCompetenceEtZone(
                competence, zone, StatutEquipe.DISPONIBLE);

        return equipes.stream()
                .map(equipe -> {
                    List<Materiel> materiels = materielRepository.findByIdEquipeAssociee(equipe.getId());
                    boolean materielDisponible = materiels.stream().anyMatch(Materiel::estDisponible);
                    return EquipeDisponibleResponse.depuis(equipe, materielDisponible);
                })
                .toList();
    }

    public Equipe changerStatut(UUID id, StatutEquipe nouveauStatut) {
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Équipe introuvable"));
        equipe.changerStatut(nouveauStatut);
        return equipeRepository.save(equipe);
    }
}