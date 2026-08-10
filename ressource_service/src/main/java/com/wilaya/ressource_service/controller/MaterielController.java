package com.wilaya.ressource_service.controller;

import com.wilaya.ressource_service.dto.ChangerStatutRequest;
import com.wilaya.ressource_service.dto.CreerMaterielRequest;
import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.model.StatutMateriel;
import com.wilaya.ressource_service.service.MaterielService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/materiels")
public class MaterielController {

    private final MaterielService materielService;

    public MaterielController(MaterielService materielService) {
        this.materielService = materielService;
    }

    @GetMapping
    public ResponseEntity<List<Materiel>> listerMateriels() {
        return ResponseEntity.ok(materielService.listerMateriels());
    }

    @PostMapping
    public ResponseEntity<Materiel> ajouterMateriel(@Valid @RequestBody CreerMaterielRequest request) {
        Materiel materiel = materielService.ajouterMateriel(request);
        return ResponseEntity.status(201).body(materiel);
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Materiel> changerStatut(
            @PathVariable UUID id,
            @Valid @RequestBody ChangerStatutRequest request) {
        StatutMateriel statut = StatutMateriel.valueOf(request.statut());
        return ResponseEntity.ok(materielService.changerStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerMateriel(@PathVariable UUID id) {
        materielService.supprimerMateriel(id);
        return ResponseEntity.noContent().build();
    }
}