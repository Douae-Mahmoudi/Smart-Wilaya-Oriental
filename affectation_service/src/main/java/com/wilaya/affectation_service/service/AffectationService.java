package com.wilaya.affectation_service.service;

import com.wilaya.affectation_service.client.RessourceServiceClient;
import com.wilaya.affectation_service.exception.AucuneEquipeDisponibleException;
import com.wilaya.affectation_service.exception.RessourceNonTrouveeException;
import com.wilaya.affectation_service.messaging.EquipeAffecteeEvent;
import com.wilaya.affectation_service.messaging.EquipeAffecteePublisher;
import com.wilaya.affectation_service.messaging.NotificationEvent;
import com.wilaya.affectation_service.messaging.NotificationPublisher;
import com.wilaya.affectation_service.messaging.SignalementClassifieEvent;
import com.wilaya.affectation_service.model.EquipeCandidate;
import com.wilaya.affectation_service.model.SignalementInfo;
import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;
import com.wilaya.affectation_service.repository.TentativeAffectationRepository;
import com.wilaya.affectation_service.scoring.CalculateurScore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AffectationService {

    private static final int DUREE_VALIDITE_MINUTES = 15;

    private static final UUID AUCUNE_EQUIPE_DISPONIBLE = new UUID(0L, 0L);

    private final TentativeAffectationRepository repository;
    private final RessourceServiceClient ressourceServiceClient;
    private final CalculateurScore calculateurScore;
    private final EquipeAffecteePublisher equipeAffecteePublisher;
    private final NotificationPublisher notificationPublisher;

    public AffectationService(
            TentativeAffectationRepository repository,
            RessourceServiceClient ressourceServiceClient,
            CalculateurScore calculateurScore,
            EquipeAffecteePublisher equipeAffecteePublisher,
            NotificationPublisher notificationPublisher) {
        this.repository = repository;
        this.ressourceServiceClient = ressourceServiceClient;
        this.calculateurScore = calculateurScore;
        this.equipeAffecteePublisher = equipeAffecteePublisher;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public void demarrerAffectation(SignalementClassifieEvent event) {
        try {
            demarrerAffectation(event, Set.of());
        } catch (AucuneEquipeDisponibleException e) {
            creerTentativePlaceholderSansEquipe(event);
        }
    }

    private void creerTentativePlaceholderSansEquipe(SignalementClassifieEvent event) {
        TentativeAffectation tentative = new TentativeAffectation(
                event.signalementId(), AUCUNE_EQUIPE_DISPONIBLE, 0.0, DUREE_VALIDITE_MINUTES,
                event.type(), event.gravite(), event.zone(),
                event.description(), event.adresse());
        tentative.refuser();
        repository.save(tentative);
    }

    private void demarrerAffectation(SignalementClassifieEvent event, Set<UUID> equipesExclues) {
        List<EquipeCandidate> candidates = ressourceServiceClient.trouverEquipesDisponibles(
                        event.type(), event.zone())
                .stream()
                .filter(c -> !equipesExclues.contains(c.id()))
                .toList();

        if (candidates.isEmpty()) {
            throw new AucuneEquipeDisponibleException(
                    "Aucune équipe disponible pour le signalement " + event.signalementId());
        }

        SignalementInfo signalementInfo = new SignalementInfo(
                event.signalementId(), event.type(), event.gravite(), event.zone());

        EquipeCandidate meilleureEquipe = candidates.stream()
                .max(Comparator.comparingDouble(c -> calculateurScore.calculer(c, signalementInfo)))
                .orElseThrow(() -> new AucuneEquipeDisponibleException("Aucune équipe candidate valide"));

        Double score = calculateurScore.calculer(meilleureEquipe, signalementInfo);

        TentativeAffectation tentative = new TentativeAffectation(
                event.signalementId(), meilleureEquipe.id(), score, DUREE_VALIDITE_MINUTES,
                event.type(), event.gravite(), event.zone(),
                event.description(), event.adresse());
        repository.save(tentative);

        notificationPublisher.publier(new NotificationEvent(
                meilleureEquipe.id(), event.signalementId(),
                "Nouvelle intervention proposée pour votre équipe"));
    }

    @Transactional
    public TentativeAffectation accepter(UUID idTentative, UUID idEquipe, UUID idAgent) {
        TentativeAffectation tentative = repository.findById(idTentative)
                .orElseThrow(() -> new RessourceNonTrouveeException("Tentative introuvable : " + idTentative));

        if (!tentative.getIdEquipeProposee().equals(idEquipe)) {
            throw new IllegalStateException("Cette tentative ne concerne pas cette équipe");
        }

        tentative.accepter(idAgent);
        repository.save(tentative);

        equipeAffecteePublisher.publier(new EquipeAffecteeEvent(
                tentative.getIdSignalement(), tentative.getIdEquipeProposee(), LocalDateTime.now()));

        return tentative;
    }

    @Transactional
    public TentativeAffectation refuser(UUID idTentative, UUID idEquipe) {
        TentativeAffectation tentative = repository.findById(idTentative)
                .orElseThrow(() -> new RessourceNonTrouveeException("Tentative introuvable : " + idTentative));

        if (!tentative.getIdEquipeProposee().equals(idEquipe)) {
            throw new IllegalStateException("Cette tentative ne concerne pas cette équipe");
        }

        tentative.refuser();
        repository.save(tentative);

        reaffecterEnCascade(tentative);

        return tentative;
    }

    @Transactional
    public TentativeAffectation affecterManuellement(UUID idSignalement, UUID idEquipe) {
        TentativeAffectation derniereConnue = repository.findByIdSignalement(idSignalement).stream()
                .max(Comparator.comparing(TentativeAffectation::getDateProposition))
                .orElse(null);

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.0, DUREE_VALIDITE_MINUTES,
                derniereConnue != null ? derniereConnue.getCategorie() : null,
                derniereConnue != null ? derniereConnue.getGravite() : null,
                derniereConnue != null ? derniereConnue.getZone() : null,
                derniereConnue != null ? derniereConnue.getDescription() : null,
                derniereConnue != null ? derniereConnue.getAdresse() : null);
        repository.save(tentative);


        return tentative;
    }

    public List<TentativeAffectation> listerEnAttente() {
        return repository.findByStatut(StatutTentative.EN_ATTENTE);
    }


    public List<TentativeAffectation> listerPourAgentEtEquipe(UUID idEquipe, UUID idAgent) {
        List<TentativeAffectation> toutes = repository.findByIdEquipeProposee(idEquipe);

        return toutes.stream()
                .filter(t -> {
                    if (t.getStatut() == StatutTentative.EN_ATTENTE) {
                        return true;
                    }
                    if (t.getStatut() == StatutTentative.ACCEPTEE) {
                        return idAgent != null && t.getIdAgentAccepteur() != null
                                && t.getIdAgentAccepteur().equals(idAgent);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<TentativeAffectation> listerPourEquipe(UUID idEquipe) {
        return repository.findByIdEquipeProposee(idEquipe);
    }


    public List<TentativeAffectation> listerSansEquipe() {
        List<TentativeAffectation> refusees = repository.findByStatut(StatutTentative.REFUSEE);

        Map<UUID, TentativeAffectation> derniereRefuseeParSignalement = new LinkedHashMap<>();
        for (TentativeAffectation t : refusees) {
            derniereRefuseeParSignalement.merge(t.getIdSignalement(), t,
                    (existante, nouvelle) ->
                            nouvelle.getDateProposition().isAfter(existante.getDateProposition()) ? nouvelle : existante);
        }

        return derniereRefuseeParSignalement.values().stream()
                .filter(t -> !aUneTentativeActive(t.getIdSignalement()))
                .collect(Collectors.toList());
    }

    private boolean aUneTentativeActive(UUID idSignalement) {
        return repository.findByIdSignalement(idSignalement).stream()
                .anyMatch(t -> t.getStatut() == StatutTentative.EN_ATTENTE || t.getStatut() == StatutTentative.ACCEPTEE);
    }

    private void reaffecterEnCascade(TentativeAffectation tentativeRefusee) {
        Set<UUID> equipesDejaProposees = repository.findByIdSignalement(tentativeRefusee.getIdSignalement())
                .stream()
                .map(TentativeAffectation::getIdEquipeProposee)
                .collect(Collectors.toSet());

        SignalementClassifieEvent event = new SignalementClassifieEvent(
                tentativeRefusee.getIdSignalement(),
                null,
                tentativeRefusee.getCategorie(),
                tentativeRefusee.getZone(),
                tentativeRefusee.getGravite(),
                null,
                tentativeRefusee.getDescription(),
                tentativeRefusee.getAdresse());
        try {
            demarrerAffectation(event, equipesDejaProposees);
        } catch (AucuneEquipeDisponibleException e) {
        }
    }
}






















































