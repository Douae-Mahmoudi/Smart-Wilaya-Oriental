package com.wilaya.signalement_service.model;

import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "signalement")
public class Signalement {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CARACTERES_SUIVI = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private static final Map<StatutSignalement, Set<StatutSignalement>> TRANSITIONS_VALIDES = new EnumMap<>(StatutSignalement.class);

    static {
        TRANSITIONS_VALIDES.put(StatutSignalement.SIGNALE, EnumSet.of(StatutSignalement.CLASSIFIE, StatutSignalement.CLOTURE));
        TRANSITIONS_VALIDES.put(StatutSignalement.CLASSIFIE, EnumSet.of(StatutSignalement.EN_RECHERCHE_EQUIPE, StatutSignalement.CLOTURE));
        TRANSITIONS_VALIDES.put(StatutSignalement.EN_RECHERCHE_EQUIPE, EnumSet.of(StatutSignalement.AFFECTE, StatutSignalement.CLOTURE));
        TRANSITIONS_VALIDES.put(StatutSignalement.AFFECTE, EnumSet.of(StatutSignalement.EN_INTERVENTION, StatutSignalement.EN_RECHERCHE_EQUIPE, StatutSignalement.CLOTURE));
        TRANSITIONS_VALIDES.put(StatutSignalement.EN_INTERVENTION, EnumSet.of(StatutSignalement.RESOLU, StatutSignalement.CLOTURE));
        TRANSITIONS_VALIDES.put(StatutSignalement.RESOLU, EnumSet.of(StatutSignalement.CLOTURE, StatutSignalement.EN_INTERVENTION));
        TRANSITIONS_VALIDES.put(StatutSignalement.CLOTURE, EnumSet.noneOf(StatutSignalement.class));
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "numero_suivi", unique = true, nullable = false, updatable = false)
    private String numeroSuivi;

    @Column(name = "cin_declarant", nullable = false)
    private String cinDeclarant;

    @Enumerated(EnumType.STRING)
    private TypeIntervention type;

    @Column(length = 2000)
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    private String zone;

    @Enumerated(EnumType.STRING)
    private NiveauGravite gravite;

    @Enumerated(EnumType.STRING)
    private StatutSignalement statut;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    protected Signalement() {
    }

    public Signalement(String cinDeclarant, TypeIntervention type, String description,
                       String photoUrl, String zone, NiveauGravite gravite) {
        this.numeroSuivi = genererNumeroSuivi();
        this.cinDeclarant = cinDeclarant;
        this.type = type;
        this.description = description;
        this.photoUrl = photoUrl;
        this.zone = zone;
        this.gravite = gravite;
        this.statut = StatutSignalement.SIGNALE;
        this.dateCreation = LocalDateTime.now();
    }

    private static String genererNumeroSuivi() {
        StringBuilder suffixe = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            suffixe.append(CARACTERES_SUIVI.charAt(RANDOM.nextInt(CARACTERES_SUIVI.length())));
        }
        String date = LocalDateTime.now().toLocalDate().toString().replace("-", "");
        return "SIG-" + date + "-" + suffixe;
    }


    public void changerStatut(StatutSignalement nouveauStatut) {
        Set<StatutSignalement> transitionsAutorisees = TRANSITIONS_VALIDES.get(this.statut);
        if (transitionsAutorisees == null || !transitionsAutorisees.contains(nouveauStatut)) {
            throw new IllegalStateException(
                    "Transition invalide : impossible de passer de " + this.statut + " a " + nouveauStatut);
        }
        this.statut = nouveauStatut;
    }


    public String masquerCin() {
        if (cinDeclarant == null || cinDeclarant.length() < 4) {
            return "****";
        }
        String debut = cinDeclarant.substring(0, 2);
        String fin = cinDeclarant.substring(cinDeclarant.length() - 2);
        return debut + "*".repeat(Math.max(cinDeclarant.length() - 4, 2)) + fin;
    }

    public UUID getId() { return id; }
    public String getNumeroSuivi() { return numeroSuivi; }
    public String getCinDeclarant() { return cinDeclarant; }
    public TypeIntervention getType() { return type; }
    public String getDescription() { return description; }
    public String getPhotoUrl() { return photoUrl; }
    public String getZone() { return zone; }
    public NiveauGravite getGravite() { return gravite; }
    public StatutSignalement getStatut() { return statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}
