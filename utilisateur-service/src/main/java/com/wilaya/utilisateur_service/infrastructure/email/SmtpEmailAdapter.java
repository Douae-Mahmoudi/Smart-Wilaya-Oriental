package com.wilaya.utilisateur_service.infrastructure.email;

import com.wilaya.utilisateur_service.domain.port.out.EmailSenderPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    public SmtpEmailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void envoyerIdentifiantsTemporaires(String destinataire, String email, String motDePasseTemporaire) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject("Votre compte a été créé");
        message.setText("""
                Bonjour,

                Un compte vous a été créé sur la plateforme de gestion des interventions.

                Identifiant : %s
                Mot de passe temporaire : %s

                Vous devrez le changer lors de votre première connexion.
                """.formatted(email, motDePasseTemporaire));
        mailSender.send(message);
    }

    @Override
    public void envoyerCodeReinitialisation(String destinataire, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject("Code de réinitialisation de votre mot de passe");
        message.setText("""
                Bonjour,

                Voici votre code de réinitialisation : %s

                Ce code est valable 15 minutes. Si vous n'êtes pas à l'origine de cette demande,
                ignorez cet email.
                """.formatted(code));
        mailSender.send(message);
    }
}
