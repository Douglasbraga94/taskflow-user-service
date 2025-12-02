package com.taskflow.userservice.domain.service;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.model.Role;
import com.taskflow.userservice.domain.port.input.AuthManagerPort;
import com.taskflow.userservice.domain.port.output.JwtGeneratorPort;
import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


/**
 * Implementação do Port de Entrada AuthManagerPort.
 * Contém a lógica de negócio de registro e autenticação.
 */
public class AuthManagerService implements AuthManagerPort {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtGeneratorPort jwtGeneratorPort;
    private final PasswordEncoder passwordEncoder;

    public AuthManagerService(UserRepositoryPort userRepositoryPort,
                              JwtGeneratorPort jwtGeneratorPort,
                              PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtGeneratorPort = jwtGeneratorPort;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Implementação do Use Case: Registrar Usuário ---
    @Override
    public User registerUser(User user) {
        // CORREÇÃO 1: Usando isPresent() com 'P' maiúsculo.
        if (userRepositoryPort.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        // Restante do código...
        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setRole(Role.MEMBER);
        user.activate();

        return userRepositoryPort.save(user);
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
}