package com.wilaya.utilisateur_service.domain.port.in;

import java.util.UUID;

public interface ModifierProfilUseCase {
    void modifierProfil(UUID idKeycloak, String nom, String prenom, String telephone, boolean notificationsActivees);
}