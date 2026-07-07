package com.wilaya.utilisateur_service.api;

import com.wilaya.utilisateur_service.api.dto.DemandeResetRequest;
import com.wilaya.utilisateur_service.api.dto.VerifierCodeRequest;
import com.wilaya.utilisateur_service.domain.port.in.DemanderReinitialisationUseCase;
import com.wilaya.utilisateur_service.domain.port.in.VerifierCodeUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/utilisateurs/mot-de-passe-oublie")
public class ReinitialisationController {

    private static final String MESSAGE_GENERIQUE =
            "Si cet email existe dans notre système, un code de vérification a été envoyé.";

    private final DemanderReinitialisationUseCase demanderReinitialisationUseCase;
    private final VerifierCodeUseCase verifierCodeUseCase;

    public ReinitialisationController(DemanderReinitialisationUseCase demanderReinitialisationUseCase,
                                       VerifierCodeUseCase verifierCodeUseCase) {
        this.demanderReinitialisationUseCase = demanderReinitialisationUseCase;
        this.verifierCodeUseCase = verifierCodeUseCase;
    }

    @PostMapping
    public ResponseEntity<String> demander(@Valid @RequestBody DemandeResetRequest request) {
        demanderReinitialisationUseCase.demanderReinitialisation(request.email());
        return ResponseEntity.ok(MESSAGE_GENERIQUE);
    }

    @PostMapping("/verifier")
    public ResponseEntity<String> verifier(@Valid @RequestBody VerifierCodeRequest request) {
        verifierCodeUseCase.verifierEtReinitialiser(
                request.email(), request.code(), request.nouveauMotDePasse()
        );
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }
}
