package com.taskflow.userservice.infrastructure.messaging;

import com.taskflow.userservice.domain.event.UserCreatedEvent;
import com.taskflow.userservice.domain.port.output.UserEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adapter de infraestrutura que implementa a Porta de Saída (Output Port)
 * para publicação de eventos usando RabbitMQ.
 */
@Component
public class RabbitMQUserEventPublisher implements UserEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user}")
    private String userExchange;

    @Value("${rabbitmq.routing.user.created}")
    private String userCreatedRoutingKey;

    public RabbitMQUserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica o evento de criação de usuário na Exchange Topic definida.
     */
    @Override
    public void publishUserCreatedEvent(UserCreatedEvent event) {
        System.out.println("-> [UserService] Publicando evento UserCreatedEvent para: " + event.email());

        // Envia a mensagem para a Exchange, usando a Routing Key específica.
        rabbitTemplate.convertAndSend(userExchange, userCreatedRoutingKey, event);

        System.out.println("-> [UserService] Evento publicado com sucesso.");
    }
} 