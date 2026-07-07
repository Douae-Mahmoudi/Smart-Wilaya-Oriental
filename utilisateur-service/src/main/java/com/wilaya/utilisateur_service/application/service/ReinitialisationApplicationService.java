package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.DemandeReinitialisation;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.in.DemanderReinitialisationUseCase;
import com.wilaya.utilisateur_service.domain.port.in.VerifierCodeUseCase;
import com.wilaya.utilisateur_service.domain.port.out.DemandeReinitialisationRepository;
import com.wilaya.utilisateur_service.domain.port.out.EmailSenderPort;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import com.wilaya.utilisateur_service.domain.service.GenerateurCode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReinitialisationApplicationService
        implements DemanderReinitialisationUseCase, VerifierCodeUseCase {

    private static final int MAX_DEMANDES_PAR_FENETRE = 3;
    private static final Duration FENETRE_ANTI_SPAM = Duration.ofMinutes(10);

    private final ProfilUtilisateurRepository profilRepository;
    private final DemandeReinitialisationRepository demandeRepository;
    private final IdentiteProviderPort identiteProvider;
    private final EmailSenderPort emailSender;
    private final GenerateurCode generateurCode = new GenerateurCode();

    public ReinitialisationApplicationService(ProfilUtilisateurRepository profilRepository,
                                               DemandeReinitialisationRepository demandeRepository,
                                               IdentiteProviderPort identiteProvider,
                                               EmailSenderPort emailSender) {
        this.profilRepository = profilRepository;
        this.demandeRepository = demandeRepository;
        this.identiteProvider = identiteProvider;
        this.emailSender = emailSender;
    }

    @Override
    public void demanderReinitialisation(String email) {
        Optional<ProfilUtilisateur> profilOpt = profilRepository.findByEmail(email);

        if (profilOpt.isEmpty()) {
            return;
        }

        ProfilUtilisateur profil = profilOpt.get();
        UUID idUtilisateur = profil.getIdKeycloak();

        long demandesRecentes = demandeRepository.compterDemandesRecentes(idUtilisateur, FENETRE_ANTI_SPAM);
        if (demandesRecentes >= MAX_DEMANDES_PAR_FENETRE) {
            return;
        }

        String code = generateurCode.genererCodeSixChiffres();
        DemandeReinitialisation demande = DemandeReinitialisation.creer(idUtilisateur, code);
        demandeRepository.save(demande);

        emailSender.envoyerCodeReinitialisation(email, code);
    }

    @Override
    public void verifierEtReinitialiser(String email, String code, String nouveauMotDePasse) {
        ProfilUtilisateur profil = profilRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Demande invalide"));

        DemandeReinitialisation demande = demandeRepository
                .findEnAttenteParUtilisateur(profil.getIdKeycloak(), code)
                .orElseThrow(() -> new IllegalArgumentException("Code invalide"));

        demande.verifierCode(code);
        demandeRepository.save(demande);

        identiteProvider.changerMotDePasse(profil.getIdKeycloak(), nouveauMotDePasse, false);
    }
}
