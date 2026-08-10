package com.wilaya.signalement_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "changement_statut")
public class ChangementStatut {

    private static final ZoneId FUSEAU_MAROC = ZoneId.of("Africa/Casablanca");

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signalement_id", nullable = false)
    private Signalement signalement;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouveau_statut", nullable = false)
    private StatutSignalement nouveauStatut;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "date_changement", nullable = false, updatable = false)
    private LocalDateTime dateChangement;

    protected ChangementStatut() {
    }

    public ChangementStatut(Signalement signalement, StatutSignalement nouveauStatut, String message) {
        this.signalement = signalement;
        this.nouveauStatut = nouveauStatut;
        this.message = message;
        this.dateChangement = LocalDateTime.now(FUSEAU_MAROC);
    }

    public UUID getId() { return id; }
    public Signalement getSignalement() { return signalement; }
    public StatutSignalement getNouveauStatut() { return nouveauStatut; }
    public String getMessage() { return message; }
    public LocalDateTime getDateChangement() { return dateChangement; }
}






































































































































