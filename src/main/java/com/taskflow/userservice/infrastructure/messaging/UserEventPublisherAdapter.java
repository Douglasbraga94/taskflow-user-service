package com.taskflow.userservice.infrastructure.messaging;

import com.taskflow.userservice.domain.event.UserCreatedEvent;
import com.taskflow.userservice.domain.port.output.UserEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter; // NOVO IMPORT
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Adaptador de Saída que implementa a porta UserEventPublisherPort.
 * Responsável por publicar eventos do domínio no RabbitMQ.
 */
@Component
public class UserEventPublisherAdapter implements UserEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(UserEventPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user}")
    private String userExchangeName;

    @Value("${rabbitmq.routing.user.created}")
    private String userCreatedRoutingKey;

    /**
     * Construtor que injeta o RabbitTemplate e o MessageConverter.
     * O MessageConverter é injetado aqui e configurado no template para
     * garantir que o Produtor use o formato JSON (e não a serialização Java).
     */
    public UserEventPublisherAdapter(RabbitTemplate rabbitTemplate, MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        // ESSENCIAL: Configurar o MessageConverter no template para forçar o JSON.
        this.rabbitTemplate.setMessageConverter(messageConverter);
    }

    @Override
    public void publishUserCreatedEvent(UserCreatedEvent event) {
        log.info("-> [UserService] Evento UserCreatedEvent publicado para a exchange: {} | User ID: {}",
                userExchangeName, event.userId());

        // O RabbitTemplate, agora configurado com o MessageConverter JSON,
        // serializa o objeto 'event' como JSON antes de enviar.
        rabbitTemplate.convertAndSend(userExchangeName, userCreatedRoutingKey, event);
    }
}