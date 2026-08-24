package com.wilaya.signalement_service.service;

import java.time.LocalDateTime;
import com.wilaya.signalement_service.dto.CreerSignalementRequest;
import com.wilaya.signalement_service.dto.SimilaireResoluResponse;
import com.wilaya.signalement_service.dto.StatistiquesSignalementResponse;
import com.wilaya.signalement_service.dto.VerifierSimilaireResoluRequest;
import com.wilaya.signalement_service.exception.DoublonSignalementException;
import com.wilaya.signalement_service.exception.RessourceNonTrouveeException;
import com.wilaya.signalement_service.messaging.SignalementEventPublisher;
import com.wilaya.signalement_service.model.ChangementStatut;
import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.policy.PolicyCalculGravite;
import com.wilaya.signalement_service.policy.ValidateurCIN;
import com.wilaya.signalement_service.repository.SignalementRepository;
import com.wilaya.signalement_service.storage.FileStorageService;
import com.wilaya.signalement_service.util.HaversineUtil;
import com.wilaya.signalement_service.util.TextPreprocessor;
import com.wilaya.signalement_service.util.TfIdfVectorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SignalementService {

    private static final Logger log = LoggerFactory.getLogger(SignalementService.class);

    private static final List<StatutSignalement> STATUTS_EXCLUS_DOUBLON =
            List.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE);
    private static final List<StatutSignalement> STATUTS_TERMINES =
            List.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE);

    private final SignalementRepository repository;
    private final PolicyCalculGravite policyCalculGravite;
    private final ValidateurCIN validateurCIN;
    private final SignalementEventPublisher eventPublisher;
    private final FileStorageService fileStorageService;
    private final TextSimilarityService textSimilarityService;
    private final TfIdfVectorizer vectorizer;

    @Value("${signalement.doublon.distance.max-meters:300}")
    private double maxDistanceMeters;

    @Value("${signalement.doublon.distance.quasi-exacte-meters:20}")
    private double distanceQuasiExacteMeters;

    @Value("${signalement.doublon.similarity.threshold:0.6}")
    private double similarityThreshold;

    @Value("${signalement.doublon.similarity.threshold.localisation-exacte:0.3}")
    private double similarityThresholdLocalisationExacte;

    @Value("${signalement.similaire-resolu.delai-jours:3}")
    private int delaiSimilaireResoluJours;

    public SignalementService(SignalementRepository repository,
                              PolicyCalculGravite policyCalculGravite,
                              ValidateurCIN validateurCIN,
                              SignalementEventPublisher eventPublisher,
                              FileStorageService fileStorageService,
                              TextSimilarityService textSimilarityService,
                              TfIdfVectorizer vectorizer) {
        this.repository = repository;
        this.policyCalculGravite = policyCalculGravite;
        this.validateurCIN = validateurCIN;
        this.eventPublisher = eventPublisher;
        this.fileStorageService = fileStorageService;
        this.textSimilarityService = textSimilarityService;
        this.vectorizer = vectorizer;
    }

    public Signalement creerSignalement(CreerSignalementRequest request, MultipartFile photo) {
        if (!validateurCIN.estValide(request.cinDeclarant())) {
            throw new IllegalArgumentException("Le format du CIN declare est invalide");
        }

        verifierDoublon(request);

        var gravite = policyCalculGravite.calculer(request.type(), request.zone());
        String photoUrl = (photo != null && !photo.isEmpty()) ? fileStorageService.sauvegarder(photo) : null;

        Signalement signalement = new Signalement(
                request.cinDeclarant(), request.type(), request.description(),
                photoUrl, request.zone(), gravite, request.adresse(),
                request.latitude(), request.longitude()
        );

        changerStatutSecurise(signalement, StatutSignalement.CLASSIFIE);
        signalement.ajouterChangementStatut(StatutSignalement.CLASSIFIE,
                "Signalement classifié automatiquement après création.");

        Signalement sauvegarde = repository.save(signalement);

        List<String> descriptions = repository.findAll().stream()
                .map(Signalement::getDescription)
                .filter(desc -> desc != null && !desc.isBlank())
                .map(TextPreprocessor::stem)
                .collect(Collectors.toList());
        vectorizer.rebuildIndex(descriptions);

        eventPublisher.publierSignalementClassifie(sauvegarde);
        return sauvegarde;
    }

    @Transactional(readOnly = true)
    public SimilaireResoluResponse verifierSimilaireResolu(VerifierSimilaireResoluRequest request) {

        List<Signalement> candidats = repository.findByTypeAndStatut(request.type(), StatutSignalement.RESOLU);
        LocalDateTime seuilTemporel = LocalDateTime.now().minusDays(delaiSimilaireResoluJours);

        for (Signalement existant : candidats) {
            LocalDateTime dateResolution = trouverDateDerniereResolution(existant);
            if (dateResolution == null || dateResolution.isBefore(seuilTemporel)) {
                continue;
            }

            Double distance = null;
            boolean sameLocation;
            if (request.latitude() != null && request.longitude() != null
                    && existant.getLatitude() != null && existant.getLongitude() != null) {
                distance = HaversineUtil.distanceInMeters(
                        request.latitude(), request.longitude(),
                        existant.getLatitude(), existant.getLongitude()
                );
                sameLocation = distance <= maxDistanceMeters;
            } else {
                sameLocation = request.zone() != null && request.zone().equalsIgnoreCase(existant.getZone());
            }

            if (!sameLocation) {
                continue;
            }

            double similarity = textSimilarityService.computeSimilarity(request.description(), existant.getDescription());
            boolean localisationQuasiExacte = distance != null && distance <= distanceQuasiExacteMeters;
            double seuilApplique = localisationQuasiExacte ? similarityThresholdLocalisationExacte : similarityThreshold;

            log.info("Vérification similaire résolu avec {} : similarité={} (seuil={}, résolu le {})",
                    existant.getNumeroSuivi(), similarity, seuilApplique, dateResolution);

            if (similarity >= seuilApplique) {
                return new SimilaireResoluResponse(true, existant.getNumeroSuivi());
            }
        }

        return new SimilaireResoluResponse(false, null);
    }

    private LocalDateTime trouverDateDerniereResolution(Signalement signalement) {
        return signalement.getHistoriqueStatuts().stream()
                .filter(c -> c.getNouveauStatut() == StatutSignalement.RESOLU)
                .map(ChangementStatut::getDateChangement)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private void verifierDoublon(CreerSignalementRequest request) {
        List<Signalement> candidats = repository.findByTypeAndStatutNotIn(
                request.type(), STATUTS_EXCLUS_DOUBLON);

        log.info("Vérification doublon : {} candidats trouvés pour le type {}", candidats.size(), request.type());

        for (Signalement existant : candidats) {
            Double distance = null;
            boolean sameLocation;

            if (request.latitude() != null && request.longitude() != null
                    && existant.getLatitude() != null && existant.getLongitude() != null) {
                distance = HaversineUtil.distanceInMeters(
                        request.latitude(), request.longitude(),
                        existant.getLatitude(), existant.getLongitude()
                );
                log.info("Distance entre les coordonnées : {} mètres (seuil = {} m)", distance, maxDistanceMeters);
                sameLocation = distance <= maxDistanceMeters;
            } else {
                sameLocation = request.zone().equalsIgnoreCase(existant.getZone());
                log.info("Comparaison de zone (fallback) : '{}' vs '{}' => {}", request.zone(), existant.getZone(), sameLocation);
            }

            if (!sameLocation) {
                log.info("Localisation différente, on passe au candidat suivant.");
                continue;
            }

            double similarity = textSimilarityService.computeSimilarity(
                    request.description(), existant.getDescription()
            );

            boolean localisationQuasiExacte = distance != null && distance <= distanceQuasiExacteMeters;
            double seuilApplique = localisationQuasiExacte ? similarityThresholdLocalisationExacte : similarityThreshold;

            log.info("Similarité avec {} : {} (seuil appliqué : {}, localisation quasi exacte : {})",
                    existant.getNumeroSuivi(), similarity, seuilApplique, localisationQuasiExacte);

            if (similarity >= seuilApplique) {
                throw new DoublonSignalementException(
                        "Un signalement similaire existe déjà pour ce type et cette localisation. " +
                                "Numéro de suivi existant : " + existant.getNumeroSuivi()
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public Signalement trouverParNumeroSuivi(String numeroSuivi) {
        return repository.findByNumeroSuivi(numeroSuivi)
                .orElseThrow(() -> new RessourceNonTrouveeException(
                        "Aucun signalement trouve pour le numero de suivi " + numeroSuivi));
    }

    @Transactional(readOnly = true)
    public Signalement trouverParId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Signalement introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Signalement> listerTout() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public StatistiquesSignalementResponse calculerStatistiques() {
        List<Signalement> tous = repository.findAll();
        long total = tous.size();
        long resolus = tous.stream()
                .filter(s -> s.getStatut() == StatutSignalement.RESOLU)
                .count();
        long enCours = tous.stream()
                .filter(s -> !STATUTS_TERMINES.contains(s.getStatut()))
                .count();
        long critiques = tous.stream()
                .filter(s -> s.getGravite() == NiveauGravite.HAUTE)
                .filter(s -> !STATUTS_TERMINES.contains(s.getStatut()))
                .count();
        return new StatistiquesSignalementResponse(total, enCours, resolus, critiques);
    }

    public Signalement changerStatut(UUID id, StatutSignalement nouveauStatut, String message) {
        Signalement signalement = repository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Signalement introuvable : " + id));
        changerStatutSecurise(signalement, nouveauStatut);
        signalement.ajouterChangementStatut(nouveauStatut, message);
        Signalement sauvegarde = repository.save(signalement);
        if (nouveauStatut == StatutSignalement.CLASSIFIE) {
            eventPublisher.publierSignalementClassifie(sauvegarde);
        }
        return sauvegarde;
    }

    private void changerStatutSecurise(Signalement signalement, StatutSignalement nouveauStatut) {
        StatutSignalement actuel = signalement.getStatut();
        boolean valide;
        switch (actuel) {
            case SIGNALE:
                valide = nouveauStatut == StatutSignalement.CLASSIFIE ||
                        nouveauStatut == StatutSignalement.CLOTURE;
                break;
            case CLASSIFIE:
                valide = nouveauStatut == StatutSignalement.EN_RECHERCHE_EQUIPE ||
                        nouveauStatut == StatutSignalement.AFFECTE ||
                        nouveauStatut == StatutSignalement.EN_INTERVENTION ||
                        nouveauStatut == StatutSignalement.RESOLU;
                break;
            case EN_RECHERCHE_EQUIPE:
                valide = nouveauStatut == StatutSignalement.AFFECTE ||
                        nouveauStatut == StatutSignalement.EN_INTERVENTION;
                break;
            case AFFECTE:
                valide = nouveauStatut == StatutSignalement.EN_INTERVENTION;
                break;
            case EN_INTERVENTION:
                valide = nouveauStatut == StatutSignalement.RESOLU;
                break;
            case RESOLU:
            case CLOTURE:
                valide = false;
                break;
            default:
                valide = false;
        }
        if (!valide) {
            throw new IllegalStateException(
                    "Transition invalide : impossible de passer de " + actuel + " à " + nouveauStatut
            );
        }
        signalement.changerStatut(nouveauStatut);
    }

    public void marquerAffecte(UUID id) {
        Signalement signalement = repository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Signalement introuvable : " + id));
        StatutSignalement actuel = signalement.getStatut();
        StatutSignalement nouveauStatut = null;
        String messageAuto = null;
        if (actuel == StatutSignalement.CLASSIFIE) {
            nouveauStatut = StatutSignalement.EN_RECHERCHE_EQUIPE;
            messageAuto = "Passage automatique en recherche d'équipe suite à l'affectation.";
        } else if (actuel == StatutSignalement.EN_RECHERCHE_EQUIPE) {
            nouveauStatut = StatutSignalement.AFFECTE;
            messageAuto = "Équipe affectée automatiquement.";
        } else {
            return;
        }
        changerStatutSecurise(signalement, nouveauStatut);
        signalement.ajouterChangementStatut(nouveauStatut, messageAuto);
        repository.save(signalement);
    }
}