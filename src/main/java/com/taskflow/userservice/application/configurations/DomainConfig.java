package com.taskflow.userservice.application.configurations;

import com.taskflow.userservice.domain.port.input.AuthManagerPort;
import com.taskflow.userservice.domain.port.output.JwtGeneratorPort;
import com.taskflow.userservice.domain.port.output.UserEventPublisherPort;
import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import com.taskflow.userservice.domain.service.AuthManagerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Classe de Configuração para a Camada de Domínio.
 * Garante que o Spring injete as dependências (Portas) corretamente no Core.
 */
@Configuration
public class DomainConfig {


}