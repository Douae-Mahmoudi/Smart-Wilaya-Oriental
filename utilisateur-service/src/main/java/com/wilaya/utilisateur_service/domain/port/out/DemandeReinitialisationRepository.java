package com.wilaya.utilisateur_service.domain.port.out;

import com.wilaya.utilisateur_service.domain.model.DemandeReinitialisation;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface DemandeReinitialisationRepository {

    DemandeReinitialisation save(DemandeReinitialisation demande);

    Optional<DemandeReinitialisation> findEnAttenteParUtilisateur(UUID idUtilisateur, String code);


    long compterDemandesRecentes(UUID idUtilisateur, Duration fenetre);
}
