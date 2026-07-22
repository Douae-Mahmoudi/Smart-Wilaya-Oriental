package com.wilaya.affectation_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RabbitMQConfig.class)
@TestPropertySource(properties = {
        "rabbitmq.queue.signalement-classifie=test.queue.classifie",
        "rabbitmq.exchange.affectation=test.exchange.affectation",
        "rabbitmq.routingkey.equipe-affectee=test.routing.affectee",
        "rabbitmq.exchange.notification=test.exchange.notification",
        "rabbitmq.routing-key.notification=test.routing.notification"
})
class RabbitMQConfigTest {

    @Autowired
    private TopicExchange signalementsExchange;

    @Autowired
    private Queue signalementClassifieQueue;

    @Autowired
    private Binding bindingSignalementClassifie;

    @Autowired
    private TopicExchange affectationExchange;

    @Autowired
    private TopicExchange notificationExchange;

    @Autowired
    private Jackson2JsonMessageConverter jsonMessageConverter;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private ConnectionFactory connectionFactory;

    @Test
    void signalementsExchange_devraitEtreConfigureCorrectement() {
        assertThat(signalementsExchange).isNotNull();
        assertThat(signalementsExchange.getName()).isEqualTo(RabbitMQConfig.EXCHANGE_SIGNALEMENTS);
    }

    @Test
    void signalementClassifieQueue_devraitEtreConfigureCorrectement() {
        assertThat(signalementClassifieQueue).isNotNull();
        assertThat(signalementClassifieQueue.getName()).isEqualTo("test.queue.classifie");
        assertThat(signalementClassifieQueue.isDurable()).isTrue();
    }

    @Test
    void bindingSignalementClassifie_devraitLierQueueEtExchangeAvecRoutingKey() {
        assertThat(bindingSignalementClassifie).isNotNull();
        assertThat(bindingSignalementClassifie.getExchange()).isEqualTo(RabbitMQConfig.EXCHANGE_SIGNALEMENTS);
        assertThat(bindingSignalementClassifie.getRoutingKey()).isEqualTo(RabbitMQConfig.ROUTING_KEY_CLASSIFIE);
        assertThat(bindingSignalementClassifie.getDestination()).isEqualTo("test.queue.classifie");
    }

    @Test
    void affectationExchange_devraitEtreConfigureCorrectement() {
        assertThat(affectationExchange).isNotNull();
        assertThat(affectationExchange.getName()).isEqualTo("test.exchange.affectation");
    }

    @Test
    void notificationExchange_devraitEtreConfigureCorrectement() {
        assertThat(notificationExchange).isNotNull();
        assertThat(notificationExchange.getName()).isEqualTo("test.exchange.notification");
    }

    @Test
    void jsonMessageConverter_devraitEtreInstancie() {
        assertThat(jsonMessageConverter).isNotNull();
        assertThat(jsonMessageConverter).isInstanceOf(Jackson2JsonMessageConverter.class);
    }

    @Test
    void rabbitTemplate_devraitEtreConfigureAvecLeConvertisseur() {
        assertThat(rabbitTemplate).isNotNull();
        assertThat(rabbitTemplate.getMessageConverter()).isEqualTo(jsonMessageConverter);
    }
}