package com.wilaya.utilisateur_service.domain.port.in;

import java.util.UUID;

public interface CreerCompteUseCase {


    UUID creerCompte(String nom, String prenom, String email, String telephone, String role, UUID idEquipe);
}