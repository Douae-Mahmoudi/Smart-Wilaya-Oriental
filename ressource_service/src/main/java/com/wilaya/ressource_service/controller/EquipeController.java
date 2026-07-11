package com.wilaya.ressource_service.controller;

import com.wilaya.ressource_service.dto.ChangerStatutRequest;
import com.wilaya.ressource_service.dto.CreerEquipeRequest;
import com.wilaya.ressource_service.dto.EquipeDisponibleResponse;
import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import com.wilaya.ressource_service.model.StatutEquipe;
import com.wilaya.ressource_service.service.EquipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/equipes")
public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @PostMapping
    public ResponseEntity<Equipe> creerEquipe(@Valid @RequestBody CreerEquipeRequest request) {
        Equipe equipe = equipeService.creerEquipe(request);
        return ResponseEntity.status(201).body(equipe);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<EquipeDisponibleResponse>> equipesDisponibles(
            @RequestParam CategorieIntervention competence,
            @RequestParam String zone) {
        return ResponseEntity.ok(equipeService.trouverDisponibles(competence, zone));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Equipe> changerStatut(
            @PathVariable UUID id,
            @Valid @RequestBody ChangerStatutRequest request) {
        StatutEquipe statut = StatutEquipe.valueOf(request.statut());
        return ResponseEntity.ok(equipeService.changerStatut(id, statut));
    }
}