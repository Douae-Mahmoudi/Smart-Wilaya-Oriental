package com.wilaya.utilisateur_service.domain.port.out;

public interface EmailSenderPort {

    void envoyerIdentifiantsTemporaires(String destinataire, String email, String motDePasseTemporaire);

    void envoyerCodeReinitialisation(String destinataire, String code);
}
