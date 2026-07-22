package com.wilaya.utilisateur_service.messaging;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.port.out.AgentRepository;
import com.wilaya.utilisateur_service.domain.port.out.EmailSenderPort;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationListener {

    private final EmailSenderPort emailSenderPort;
    private final AgentRepository agentRepository;

    public NotificationListener(EmailSenderPort emailSenderPort, AgentRepository agentRepository) {
        this.emailSenderPort = emailSenderPort;
        this.agentRepository = agentRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void recevoirNotification(NotificationEvent event) {
        List<Agent> agents = agentRepository.findByIdEquipe(event.idEquipe());

        String messageSimple = "Vous avez une nouvelle mission à consulter dans l'application.";

        agents.forEach(agent ->
                emailSenderPort.envoyerNotificationGenerique(agent.getProfil().getEmail(), messageSimple)
        );
    }
}