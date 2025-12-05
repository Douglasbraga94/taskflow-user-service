package com.taskflow.userservice.domain.event;

import com.taskflow.userservice.domain.model.Role;
import java.io.Serializable;

/**
 * Evento de domínio que é publicado quando um novo usuário é criado no UserService.
 * É um contrato (record imutável) que será enviado pelo Message Broker (RabbitMQ).
 */
public record UserCreatedEvent(
        Long userId,
        String email,
        String name,
        Role role
) implements Serializable {}