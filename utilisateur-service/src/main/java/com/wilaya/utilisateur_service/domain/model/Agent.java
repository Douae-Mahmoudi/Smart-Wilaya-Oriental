package com.wilaya.utilisateur_service.domain.model;

import java.util.UUID;


public class Agent {

    private final ProfilUtilisateur profil;
    private UUID idEquipe;
    private StatutAgent statut;

    public Agent(ProfilUtilisateur profil, UUID idEquipe) {
        this.profil = profil;
        this.idEquipe = idEquipe;
        this.statut = StatutAgent.ACTIF;
    }

    public void desactiver() {
        this.statut = StatutAgent.INACTIF;
    }

    public void reactiver() {
        this.statut = StatutAgent.ACTIF;
    }

    public void reaffecterEquipe(UUID nouvelleEquipe) {
        this.idEquipe = nouvelleEquipe;
    }

    public ProfilUtilisateur getProfil() { return profil; }
    public UUID getIdEquipe() { return idEquipe; }
    public StatutAgent getStatut() { return statut; }
}
