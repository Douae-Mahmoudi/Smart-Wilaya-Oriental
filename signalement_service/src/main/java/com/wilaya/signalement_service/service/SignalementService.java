package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.dto.CreerSignalementRequest;
import com.wilaya.signalement_service.exception.DoublonSignalementException;
import com.wilaya.signalement_service.exception.RessourceNonTrouveeException;
import com.wilaya.signalement_service.messaging.SignalementEventPublisher;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.policy.PolicyCalculGravite;
import com.wilaya.signalement_service.policy.ValidateurCIN;
import com.wilaya.signalement_service.repository.SignalementRepository;
import com.wilaya.signalement_service.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SignalementService {

    private static final long DELAI_DOUBLON_MINUTES = 60;

    private static final List<StatutSignalement> STATUTS_EXCLUS_DOUBLON = List.of(
            StatutSignalement.RESOLU, StatutSignalement.CLOTURE
    );

    private final SignalementRepository repository;
    private final PolicyCalculGravite policyCalculGravite;
    private final ValidateurCIN validateurCIN;
    private final SignalementEventPublisher eventPublisher;
    private final FileStorageService fileStorageService;

    public SignalementService(SignalementRepository repository,
                              PolicyCalculGravite policyCalculGravite,
                              ValidateurCIN validateurCIN,
                              SignalementEventPublisher eventPublisher,
                              FileStorageService fileStorageService) {
        this.repository = repository;
        this.policyCalculGravite = policyCalculGravite;
        this.validateurCIN = validateurCIN;
        this.eventPublisher = eventPublisher;
        this.fileStorageService = fileStorageService;
    }

    public Signalement creerSignalement(CreerSignalementRequest request, MultipartFile photo) {
        if (!validateurCIN.estValide(request.cinDeclarant())) {
            throw new IllegalArgumentException("Le format du CIN declare est invalide");
        }

        verifierAbsenceDeDoublon(request);

        var gravite = policyCalculGravite.calculer(request.type(), request.zone());

        String photoUrl = (photo != null && !photo.isEmpty())
                ? fileStorageService.sauvegarder(photo)
                : null;

        Signalement signalement = new Signalement(
                request.cinDeclarant(),
                request.type(),
                request.description(),
                photoUrl,
                request.zone(),
                gravite,
                request.adresse()   // <-- NOUVEAU : adresse passée au constructeur
        );

        signalement.changerStatut(StatutSignalement.CLASSIFIE);

        Signalement signalementSauvegarde = repository.save(signalement);

        eventPublisher.publierSignalementClassifie(signalementSauvegarde);

        return signalementSauvegarde;
    }

    @Transactional(readOnly = true)
    public Signalement trouverParNumeroSuivi(String numeroSuivi) {
        return repository.findByNumeroSuivi(numeroSuivi)
                .orElseThrow(() -> new RessourceNonTrouveeException(
                        "Aucun signalement trouve pour le numero de suivi " + numeroSuivi));
    }

    @Transactional(readOnly = true)
    public List<Signalement> listerTout() {
        return repository.findAll();
    }

    public Signalement changerStatut(UUID id, StatutSignalement nouveauStatut) {
        Signalement signalement = repository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Signalement introuvable : " + id));

        signalement.changerStatut(nouveauStatut);
        Signalement sauvegarde = repository.save(signalement);

        if (nouveauStatut == StatutSignalement.CLASSIFIE) {
            eventPublisher.publierSignalementClassifie(sauvegarde);
        }

        return sauvegarde;
    }

    // Méthode de vérification des doublons modifiée
    private void verifierAbsenceDeDoublon(CreerSignalementRequest request) {
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(DELAI_DOUBLON_MINUTES);

        // Doublon par type + zone + adresse
        List<Signalement> doublonsParAdresse = repository.findByTypeAndZoneAndAdresseAndDateCreationAfterAndStatutNotIn(
                request.type(), request.zone(), request.adresse(), seuil, STATUTS_EXCLUS_DOUBLON);

        if (!doublonsParAdresse.isEmpty()) {
            throw new DoublonSignalementException(
                    "Un signalement similaire (" + request.type() + " / " + request.zone()
                            + " / " + request.adresse() + ") a deja ete enregistre recemment et est en cours de traitement. "
                            + "Numero de suivi existant : " + doublonsParAdresse.get(0).getNumeroSuivi());
        }

        // Doublon par CIN
        List<Signalement> doublonsParCin = repository.findByCinDeclarantAndDateCreationAfterAndStatutNotIn(
                request.cinDeclarant(), seuil, STATUTS_EXCLUS_DOUBLON);

        if (!doublonsParCin.isEmpty()) {
            throw new DoublonSignalementException(
                    "Vous avez deja un signalement en cours de traitement. "
                            + "Numero de suivi existant : " + doublonsParCin.get(0).getNumeroSuivi());
        }
    }
}