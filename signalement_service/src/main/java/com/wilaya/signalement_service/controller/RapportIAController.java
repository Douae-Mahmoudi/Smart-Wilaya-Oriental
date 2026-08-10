package com.wilaya.signalement_service.controller;

import com.wilaya.signalement_service.dto.RapportIAResponse;
import com.wilaya.signalement_service.service.RapportIAService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rapports-ia")
public class RapportIAController {

    private final RapportIAService rapportIAService;

    public RapportIAController(RapportIAService rapportIAService) {
        this.rapportIAService = rapportIAService;
    }

    @PostMapping("/generer")
    @PreAuthorize("hasRole('SUPERVISEUR')")
    public ResponseEntity<RapportIAResponse> generer() {
        RapportIAResponse rapport = rapportIAService.genererRapport();
        return ResponseEntity.ok(rapport);
    }
}






































































































