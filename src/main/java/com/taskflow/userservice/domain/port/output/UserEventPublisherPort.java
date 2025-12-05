package com.taskflow.userservice.domain.port.output;

import com.taskflow.userservice.domain.event.UserCreatedEvent;

/**
 * Porta de saída para publicação de eventos de domínio (Domain Events).
 * A implementação (Adapter) fará a comunicação real com o Message Broker.
 */
public interface UserEventPublisherPort {

    /**
     * Publica um evento de criação de usuário.
     * @param event O DTO de evento com os dados do usuário.
     */
    void publishUserCreatedEvent(UserCreatedEvent event);
}