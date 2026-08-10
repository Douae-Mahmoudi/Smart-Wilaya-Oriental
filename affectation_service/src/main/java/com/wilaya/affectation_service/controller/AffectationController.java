package com.wilaya.affectation_service.controller;

import com.wilaya.affectation_service.dto.AccepterAffectationRequest;
import com.wilaya.affectation_service.dto.AffectationResponse;
import com.wilaya.affectation_service.model.TentativeAffectation;
import com.wilaya.affectation_service.service.AffectationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/affectations")
public class AffectationController {

    private final AffectationService affectationService;

    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @PostMapping("/{id}/accepter")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<AffectationResponse> accepter(
            @PathVariable UUID id,
            @Valid @RequestBody AccepterAffectationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID idAgent = UUID.fromString(jwt.getSubject());
        TentativeAffectation tentative = affectationService.accepter(id, request.idEquipe(), idAgent);
        return ResponseEntity.ok(AffectationResponse.depuis(tentative));
    }

    @PostMapping("/{id}/refuser")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<AffectationResponse> refuser(
            @PathVariable UUID id,
            @Valid @RequestBody AccepterAffectationRequest request) {
        TentativeAffectation tentative = affectationService.refuser(id, request.idEquipe());
        return ResponseEntity.ok(AffectationResponse.depuis(tentative));
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('SUPERVISEUR')")
    public ResponseEntity<List<AffectationResponse>> enAttente() {
        List<AffectationResponse> reponse = affectationService.listerEnAttente().stream()
                .map(AffectationResponse::depuis)
                .toList();
        return ResponseEntity.ok(reponse);
    }

    @GetMapping("/sans-equipe")
    @PreAuthorize("hasRole('SUPERVISEUR')")
    public ResponseEntity<List<AffectationResponse>> sansEquipe() {
        List<AffectationResponse> reponse = affectationService.listerSansEquipe().stream()
                .map(AffectationResponse::depuis)
                .toList();
        return ResponseEntity.ok(reponse);
    }

    @GetMapping("/mes-affectations")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<AffectationResponse>> mesAffectations(
            @RequestParam UUID idEquipe,
            @AuthenticationPrincipal Jwt jwt) {
        UUID idAgent = UUID.fromString(jwt.getSubject());
        List<AffectationResponse> reponse = affectationService.listerPourAgentEtEquipe(idEquipe, idAgent).stream()
                .map(AffectationResponse::depuis)
                .toList();
        return ResponseEntity.ok(reponse);
    }

    @PostMapping("/{idSignalement}/affecter-manuellement")
    @PreAuthorize("hasRole('SUPERVISEUR')")
    public ResponseEntity<AffectationResponse> affecterManuellement(
            @PathVariable UUID idSignalement,
            @Valid @RequestBody AccepterAffectationRequest request) {
        TentativeAffectation tentative = affectationService.affecterManuellement(idSignalement, request.idEquipe());
        return ResponseEntity.ok(AffectationResponse.depuis(tentative));
    }
}













































































