package com.taskflow.userservice.application.configurations;

import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração de Beans globais necessários pelo Spring Security e outras camadas.
 */
@Configuration
public class ApplicationConfig {

    private final UserRepositoryPort userRepositoryPort;

    public ApplicationConfig(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Define o UserDetailsService, que é responsável por carregar os detalhes do usuário
     * para autenticação (usando o UserRepositoryPort da camada de domínio).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepositoryPort.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        // Mapeia o usuário do domínio (com.taskflow...) para o User do Spring Security
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getRole().name())
                        .disabled(!user.isActive())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));
    }

    /**
     * Define o AuthenticationProvider, que combina o UserDetailsService e o PasswordEncoder.
     * É o coração do processo de autenticação do DAO.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Define o PasswordEncoder, essencial para hashing de senhas.
     * Estamos usando BCrypt (o padrão de mercado).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define o AuthenticationManager. Ele é necessário para realizar o processo
     * de autenticação no Controller (AuthService).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}