package com.wilaya.ressource_service.service;

import com.wilaya.ressource_service.dto.CreerMaterielRequest;
import com.wilaya.ressource_service.exception.RessourceNonTrouveeException;
import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.model.StatutMateriel;
import com.wilaya.ressource_service.repository.MaterielRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MaterielService {

    private final MaterielRepository materielRepository;

    public MaterielService(MaterielRepository materielRepository) {
        this.materielRepository = materielRepository;
    }

    public Materiel ajouterMateriel(CreerMaterielRequest request) {
        Materiel materiel = new Materiel(request.type(), request.idEquipeAssociee());
        return materielRepository.save(materiel);
    }

    public List<Materiel> listerMateriels() {
        return materielRepository.findAll();
    }

    public Materiel changerStatut(UUID id, StatutMateriel nouveauStatut) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Matériel introuvable"));
        materiel.changerStatut(nouveauStatut);
        return materielRepository.save(materiel);
    }

    public void supprimerMateriel(UUID id) {
        if (!materielRepository.existsById(id)) {
            throw new RessourceNonTrouveeException("Matériel introuvable");
        }
        materielRepository.deleteById(id);
    }
}