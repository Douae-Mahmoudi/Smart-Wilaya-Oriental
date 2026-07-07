package com.wilaya.utilisateur_service.domain.port.in;

public interface VerifierCodeUseCase {

    void verifierEtReinitialiser(String email, String code, String nouveauMotDePasse);
}