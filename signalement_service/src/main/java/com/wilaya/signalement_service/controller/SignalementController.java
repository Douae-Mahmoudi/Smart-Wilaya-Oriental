package com.wilaya.signalement_service.controller;

import com.wilaya.signalement_service.dto.*;
import com.wilaya.signalement_service.exception.RessourceNonTrouveeException;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import com.wilaya.signalement_service.service.SignalementService;
import com.wilaya.signalement_service.storage.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/signalements")
public class SignalementController {

    private final SignalementService signalementService;
    private final FileStorageService fileStorageService;

    public SignalementController(SignalementService signalementService, FileStorageService fileStorageService) {
        this.signalementService = signalementService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreerSignalementResponse> creerSignalement(
            @RequestPart("data") @Valid CreerSignalementRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        Signalement signalement = signalementService.creerSignalement(request, photo);
        return ResponseEntity.status(201).body(CreerSignalementResponse.depuis(signalement));
    }

    @GetMapping("/photos/{nomFichier}")
    public ResponseEntity<Resource> obtenirPhoto(@PathVariable String nomFichier) throws IOException {
        Path fichier = fileStorageService.resoudre(nomFichier);
        Resource resource = new UrlResource(fichier.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RessourceNonTrouveeException("Photo introuvable");
        }

        String contentType = Files.probeContentType(fichier);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }

    @GetMapping("/{numeroSuivi}")
    public ResponseEntity<SignalementPublicResponse> consulterParNumeroSuivi(@PathVariable String numeroSuivi) {
        Signalement signalement = signalementService.trouverParNumeroSuivi(numeroSuivi);
        return ResponseEntity.ok(SignalementPublicResponse.depuis(signalement));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERVISEUR')")
    public ResponseEntity<List<SignalementResponse>> lister(
            @RequestParam(required = false) TypeIntervention type,
            @RequestParam(required = false) StatutSignalement statut,
            @RequestParam(required = false) String zone) {

        List<Signalement> signalements = signalementService.listerTout().stream()
                .filter(s -> type == null || s.getType() == type)
                .filter(s -> statut == null || s.getStatut() == statut)
                .filter(s -> zone == null || zone.equalsIgnoreCase(s.getZone()))
                .toList();

        List<SignalementResponse> reponse = signalements.stream()
                .map(SignalementResponse::depuis)
                .toList();

        return ResponseEntity.ok(reponse);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('AGENT', 'SUPERVISEUR')")
    public ResponseEntity<SignalementResponse> changerStatut(
            @PathVariable UUID id,
            @Valid @RequestBody ChangerStatutRequest request) {

        StatutSignalement nouveauStatut = StatutSignalement.valueOf(request.statut());
        Signalement signalement = signalementService.changerStatut(id, nouveauStatut);
        return ResponseEntity.ok(SignalementResponse.depuis(signalement));
    }
}