package com.wilaya.utilisateur_service.domain.port.in;

import java.util.UUID;

public interface ChangerMotDePasseUseCase {
    void changerMotDePasse(UUID idKeycloak, String ancienMotDePasse, String nouveauMotDePasse);
}