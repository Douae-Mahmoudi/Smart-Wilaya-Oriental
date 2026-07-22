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
import java.util.List;
import java.util.UUID;

@Service
public class AffectationService {

    private static final int DUREE_VALIDITE_MINUTES = 15;

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
        List<EquipeCandidate> candidates = ressourceServiceClient.trouverEquipesDisponibles(
                event.type(), event.zone());

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
                event.type(), event.gravite(), event.zone());
        repository.save(tentative);

        notificationPublisher.publier(new NotificationEvent(
                meilleureEquipe.id(), event.signalementId(),
                "Nouvelle intervention proposée pour votre équipe"));
    }

    @Transactional
    public TentativeAffectation accepter(UUID idTentative, UUID idEquipe) {
        TentativeAffectation tentative = repository.findById(idTentative)
                .orElseThrow(() -> new RessourceNonTrouveeException("Tentative introuvable : " + idTentative));

        if (!tentative.getIdEquipeProposee().equals(idEquipe)) {
            throw new IllegalStateException("Cette tentative ne concerne pas cette équipe");
        }

        tentative.accepter();
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
        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.0, DUREE_VALIDITE_MINUTES, null, null, null);
        tentative.accepter();
        repository.save(tentative);

        equipeAffecteePublisher.publier(new EquipeAffecteeEvent(
                idSignalement, idEquipe, LocalDateTime.now()));

        return tentative;
    }

    public List<TentativeAffectation> listerEnAttente() {
        return repository.findByStatut(StatutTentative.EN_ATTENTE);
    }

    private void reaffecterEnCascade(TentativeAffectation tentativeRefusee) {
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                tentativeRefusee.getIdSignalement(),
                null,
                tentativeRefusee.getCategorie(),
                tentativeRefusee.getZone(),
                tentativeRefusee.getGravite(),
                null);
        try {
            demarrerAffectation(event);
        } catch (AucuneEquipeDisponibleException e) {
        }
    }
}