package com.wilaya.utilisateur_service.api;

import com.wilaya.utilisateur_service.api.dto.*;
import com.wilaya.utilisateur_service.domain.port.in.*;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final ProfilUtilisateurRepository profilRepository;
    private final CreerCompteUseCase creerCompteUseCase;
    private final ModifierProfilUseCase modifierProfilUseCase;
    private final ChangerMotDePasseUseCase changerMotDePasseUseCase;
    private final ListerAgentsUseCase listerAgentsUseCase;
    private final ListerAdminsUseCase listerAdminsUseCase;
    private final ListerSuperviseursUseCase listerSuperviseursUseCase;
    private final SupprimerUtilisateurUseCase supprimerUtilisateurUseCase;

    public UtilisateurController(ProfilUtilisateurRepository profilRepository,
                                 CreerCompteUseCase creerCompteUseCase,
                                 ModifierProfilUseCase modifierProfilUseCase,
                                 ChangerMotDePasseUseCase changerMotDePasseUseCase,
                                 ListerAgentsUseCase listerAgentsUseCase,
                                 ListerAdminsUseCase listerAdminsUseCase,
                                 ListerSuperviseursUseCase listerSuperviseursUseCase,
                                 SupprimerUtilisateurUseCase supprimerUtilisateurUseCase) {
        this.profilRepository = profilRepository;
        this.creerCompteUseCase = creerCompteUseCase;
        this.modifierProfilUseCase = modifierProfilUseCase;
        this.changerMotDePasseUseCase = changerMotDePasseUseCase;
        this.listerAgentsUseCase = listerAgentsUseCase;
        this.listerAdminsUseCase = listerAdminsUseCase;
        this.listerSuperviseursUseCase = listerSuperviseursUseCase;
        this.supprimerUtilisateurUseCase = supprimerUtilisateurUseCase;
    }

    // === Endpoints existants ===

    @GetMapping("/moi")
    public ResponseEntity<ProfilResponse> monProfil(JwtAuthenticationToken authentication) {
        UUID idKeycloak = extraireIdKeycloak(authentication);
        return profilRepository.findByIdKeycloak(idKeycloak)
                .map(profil -> ResponseEntity.ok(ProfilResponse.depuis(profil)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/agents")
    public ResponseEntity<Void> creerAgent(@Valid @RequestBody CreerCompteRequest request) {
        creerCompteUseCase.creerCompte(
                request.nom(), request.prenom(), request.email(),
                request.telephone(), "AGENT", request.idEquipe()
        );
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/superviseurs")
    public ResponseEntity<Void> creerSuperviseur(@Valid @RequestBody CreerCompteRequest request) {
        creerCompteUseCase.creerCompte(
                request.nom(), request.prenom(), request.email(),
                request.telephone(), "SUPERVISEUR", null
        );
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/admins")
    public ResponseEntity<Void> creerAdmin(@Valid @RequestBody CreerCompteRequest request) {
        creerCompteUseCase.creerCompte(
                request.nom(), request.prenom(), request.email(),
                request.telephone(), "ADMIN", null
        );
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/moi")
    public ResponseEntity<Void> modifierMonProfil(JwtAuthenticationToken authentication,
                                                  @Valid @RequestBody ModifierProfilRequest request) {
        UUID idKeycloak = extraireIdKeycloak(authentication);
        modifierProfilUseCase.modifierProfil(idKeycloak, request.nom(), request.prenom(),
                request.telephone(), request.notificationsActivees());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/moi/mot-de-passe")
    public ResponseEntity<Void> changerMonMotDePasse(JwtAuthenticationToken authentication,
                                                     @Valid @RequestBody ChangerMotDePasseRequest request) {
        UUID idKeycloak = extraireIdKeycloak(authentication);
        changerMotDePasseUseCase.changerMotDePasse(idKeycloak, request.ancienMotDePasse(), request.nouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agents")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<List<AgentResponse>> listerAgentsParEquipe(@RequestParam UUID equipe) {
        List<AgentResponse> agents = listerAgentsUseCase.listerParEquipe(equipe).stream()
                .map(AgentResponse::depuis)
                .toList();
        return ResponseEntity.ok(agents);
    }

    // === NOUVEAUX ENDPOINTS ===

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AgentResponse>> listerAdmins() {
        List<AgentResponse> admins = listerAdminsUseCase.listerAdmins().stream()
                .map(AgentResponse::depuis)
                .toList();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/superviseurs")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR')")
    public ResponseEntity<List<AgentResponse>> listerSuperviseurs() {
        List<AgentResponse> superviseurs = listerSuperviseursUseCase.listerSuperviseurs().stream()
                .map(AgentResponse::depuis)
                .toList();
        return ResponseEntity.ok(superviseurs);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable String id) {
        supprimerUtilisateurUseCase.supprimer(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    // === Méthode utilitaire ===

    private UUID extraireIdKeycloak(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        return UUID.fromString(jwt.getSubject());
    }
}