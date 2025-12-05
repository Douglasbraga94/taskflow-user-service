package com.taskflow.userservice.domain.service;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.model.Role;
import com.taskflow.userservice.domain.port.input.AuthManagerPort;
import com.taskflow.userservice.domain.port.output.JwtGeneratorPort;
import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import com.taskflow.userservice.domain.port.output.UserEventPublisherPort;
import com.taskflow.userservice.domain.event.UserCreatedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Adicionar o import para @Transactional

import java.util.Optional;


/**
 * Implementação do Port de Entrada AuthManagerPort.
 * Contém a lógica de negócio de registro e autenticação.
 */
@Service
public class AuthManagerService implements AuthManagerPort {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtGeneratorPort jwtGeneratorPort;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisherPort userEventPublisherPort;

    public AuthManagerService(UserRepositoryPort userRepositoryPort,
                              JwtGeneratorPort jwtGeneratorPort,
                              PasswordEncoder passwordEncoder,
                              UserEventPublisherPort userEventPublisherPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtGeneratorPort = jwtGeneratorPort;
        this.passwordEncoder = passwordEncoder;
        this.userEventPublisherPort = userEventPublisherPort;
    }

    // --- Implementação do Use Case: Registrar Usuário (MEMBER) ---
    @Override
    @Transactional // Garante que a operação de persistência seja atômica
    public User registerUser(User user) {
        if (userRepositoryPort.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        // 1. Processamento de negócio do usuário
        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setRole(Role.MEMBER);
        user.activate();

        // 2. Persistência
        User savedUser = userRepositoryPort.save(user);

        // 3. Publicar o evento de Domínio
        UserCreatedEvent event = new UserCreatedEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole()
        );
        userEventPublisherPort.publishUserCreatedEvent(event);

        return savedUser;
    }

    // --- Implementação do Use Case: Autenticar Usuário ---
    @Override
    public Optional<String> authenticateUser(String email, String rawPassword) {
        Optional<User> userOptional = userRepositoryPort.findByEmail(email);

        if (userOptional.isEmpty()) {
            return Optional.empty(); // Usuário não encontrado
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return Optional.empty(); // Senha incorreta
        }

        String token = jwtGeneratorPort.generateToken(user);

        return Optional.of(token);
    }

    // --- Implementação do NOVO Use Case: Registrar Primeiro Administrador ---
    /**
     * Implementação adicionada para criar o primeiro usuário ADMIN na inicialização do sistema,
     * garantindo que ele só exista se a base de dados estiver vazia.
     * CRÍTICO: Usa existsAnyUser() em vez de findAll().
     */
    @Override
    @Transactional // Garante que a operação de persistência seja atômica
    public User registerFirstAdminUser(User user) {
        // Verifica se já existe algum usuário no sistema. Se existir, não faz nada.
        // Otimização: existsAnyUser() é mais performático que findAll().size()
        if (userRepositoryPort.existsAnyUser()) {
            // Retorna o usuário existente (ou null para indicar que nenhuma ação foi tomada)
            return userRepositoryPort.findByEmail(user.getEmail()).orElse(null);
        }

        // Verifica se o email já existe (mesmo que o existsAnyUser() seja false,
        // para garantir que esta chamada não seja usada de forma errada posteriormente)
        if (userRepositoryPort.findByEmail(user.getEmail()).isPresent()) {
            return userRepositoryPort.findByEmail(user.getEmail()).orElse(null);
        }


        // 1. Processamento de negócio do usuário ADMIN
        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setRole(Role.ADMIN); // Define como ADMIN
        user.activate();

        // 2. Persistência
        User savedUser = userRepositoryPort.save(user);

        // 3. Publicar o evento de Domínio
        UserCreatedEvent event = new UserCreatedEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole()
        );
        userEventPublisherPort.publishUserCreatedEvent(event);

        return savedUser;
    }
}