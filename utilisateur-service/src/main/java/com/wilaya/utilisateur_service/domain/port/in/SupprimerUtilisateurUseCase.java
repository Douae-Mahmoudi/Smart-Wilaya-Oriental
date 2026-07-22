package com.wilaya.utilisateur_service.domain.port.in;

import java.util.UUID;

public interface SupprimerUtilisateurUseCase {
    void supprimer(UUID id);
}