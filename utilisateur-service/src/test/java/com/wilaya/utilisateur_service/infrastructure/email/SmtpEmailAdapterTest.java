package com.wilaya.utilisateur_service.infrastructure.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmtpEmailAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    private SmtpEmailAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SmtpEmailAdapter(mailSender);
    }


    @Test
    void envoyerIdentifiantsTemporairesEnvoieUnMessageAuBonDestinataire() {
        adapter.envoyerIdentifiantsTemporaires("karim@example.com", "karim@example.com", "MotDePasseTemp123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("karim@example.com");
    }

    @Test
    void envoyerIdentifiantsTemporairesUtiliseLeBonSujet() {
        adapter.envoyerIdentifiantsTemporaires("karim@example.com", "karim@example.com", "MotDePasseTemp123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getSubject()).isEqualTo("Votre compte a été créé");
    }

    @Test
    void envoyerIdentifiantsTemporairesIncluLIdentifiantEtLeMotDePasseDansLeCorps() {
        adapter.envoyerIdentifiantsTemporaires("karim@example.com", "karim@example.com", "MotDePasseTemp123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        String corps = captor.getValue().getText();
        assertThat(corps).contains("karim@example.com");
        assertThat(corps).contains("MotDePasseTemp123");
    }

    @Test
    void envoyerIdentifiantsTemporairesAvecDestinataireDifferentDeLEmailEnvoieAuDestinataire() {
        adapter.envoyerIdentifiantsTemporaires("autre-destinataire@example.com", "karim@example.com", "MotDePasseTemp123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getTo()).containsExactly("autre-destinataire@example.com");
        assertThat(captor.getValue().getText()).contains("karim@example.com");
    }


    @Test
    void envoyerCodeReinitialisationEnvoieUnMessageAuBonDestinataire() {
        adapter.envoyerCodeReinitialisation("karim@example.com", "123456");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getTo()).containsExactly("karim@example.com");
    }

    @Test
    void envoyerCodeReinitialisationUtiliseLeBonSujet() {
        adapter.envoyerCodeReinitialisation("karim@example.com", "123456");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getSubject()).isEqualTo("Code de réinitialisation de votre mot de passe");
    }

    @Test
    void envoyerCodeReinitialisationIncluLeCodeDansLeCorps() {
        adapter.envoyerCodeReinitialisation("karim@example.com", "654321");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText()).contains("654321");
    }
}



















