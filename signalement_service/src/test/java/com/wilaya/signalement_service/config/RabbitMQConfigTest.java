package com.wilaya.signalement_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RabbitMQConfig.class)
@TestPropertySource(properties = {
        "rabbitmq.exchange.affectation=affectation.exchange",
        "rabbitmq.routingkey.equipe-affectee=equipe.affectee",
        "rabbitmq.queue.equipe-affectee=equipe.affectee.signalement.queue"
})
class RabbitMQConfigTest {

    @MockBean
    private ConnectionFactory connectionFactory;

    @Autowired
    private ApplicationContext context;

    @Test
    void beansShouldBeCreated() {
        assertThat(context.getBean("signalementsExchange", TopicExchange.class)).isNotNull();
        assertThat(context.getBean("affectationExchange", TopicExchange.class)).isNotNull();
        assertThat(context.getBean("signalementClassifieQueue", Queue.class)).isNotNull();
        assertThat(context.getBean("equipeAffecteeQueue", Queue.class)).isNotNull();
        assertThat(context.getBean("bindingSignalementClassifie", Binding.class)).isNotNull();
        assertThat(context.getBean("bindingEquipeAffectee", Binding.class)).isNotNull();
        assertThat(context.getBean(Jackson2JsonMessageConverter.class)).isNotNull();
        assertThat(context.getBean(RabbitTemplate.class)).isNotNull();
    }
}
