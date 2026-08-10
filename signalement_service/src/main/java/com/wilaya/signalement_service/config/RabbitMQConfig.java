package com.wilaya.signalement_service.config;

import com.wilaya.signalement_service.messaging.EquipeAffecteeEvent;
import com.wilaya.signalement_service.messaging.SignalementClassifieEvent;
import com.wilaya.signalement_service.messaging.SignalementEventPublisher;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_CLASSIFIE = "signalements.classifie.queue";

    public static final String TYPE_ID_SIGNALEMENT_CLASSIFIE = "signalementClassifieEvent";

    public static final String TYPE_ID_EQUIPE_AFFECTEE = "equipeAffecteeEvent";

    @Value("${rabbitmq.exchange.affectation}")
    private String exchangeAffectation;

    @Value("${rabbitmq.routingkey.equipe-affectee}")
    private String routingKeyEquipeAffectee;

    @Value("${rabbitmq.queue.equipe-affectee}")
    private String queueEquipeAffectee;

    @Bean
    public TopicExchange signalementsExchange() {
        return new TopicExchange(SignalementEventPublisher.EXCHANGE);
    }

    @Bean
    public Queue signalementClassifieQueue() {
        return new Queue(QUEUE_CLASSIFIE, true);
    }

    @Bean
    public Binding bindingSignalementClassifie(Queue signalementClassifieQueue, TopicExchange signalementsExchange) {
        return BindingBuilder.bind(signalementClassifieQueue)
                .to(signalementsExchange)
                .with(SignalementEventPublisher.ROUTING_KEY_CLASSIFIE);
    }


    @Bean
    public TopicExchange affectationExchange() {
        return new TopicExchange(exchangeAffectation);
    }

    @Bean
    public Queue equipeAffecteeQueue() {
        return new Queue(queueEquipeAffectee, true);
    }

    @Bean
    public Binding bindingEquipeAffectee(Queue equipeAffecteeQueue, TopicExchange affectationExchange) {
        return BindingBuilder.bind(equipeAffecteeQueue)
                .to(affectationExchange)
                .with(routingKeyEquipeAffectee);
    }


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        classMapper.setIdClassMapping(Map.of(
                TYPE_ID_SIGNALEMENT_CLASSIFIE, SignalementClassifieEvent.class,
                TYPE_ID_EQUIPE_AFFECTEE, EquipeAffecteeEvent.class
        ));
        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
