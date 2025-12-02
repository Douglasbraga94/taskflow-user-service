package com.taskflow.userservice.application.configurations;

import com.taskflow.userservice.domain.port.input.AuthManagerPort;
import com.taskflow.userservice.domain.port.output.JwtGeneratorPort;
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

    // 1. O PasswordEncoder (Infraestrutura) é criado para ser injetado no Domínio.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. O Serviço de Domínio (Core) é criado, e o Spring injeta suas dependências.
    @Bean
    public AuthManagerPort authManagerPort(
            UserRepositoryPort userRepositoryPort, // Implementado pelo UserRepositoryAdapter
            JwtGeneratorPort jwtGeneratorPort,     // Implementado pelo JwtTokenAdapter
            PasswordEncoder passwordEncoder) {      // Implementado acima

        // Retorna a implementação do serviço do Domínio
        return new AuthManagerService(userRepositoryPort, jwtGeneratorPort, passwordEncoder);
    }
}