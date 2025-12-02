package com.taskflow.userservice.application.adapters.input.api;

import com.taskflow.userservice.application.adapters.input.api.dto.LoginRequest;
import com.taskflow.userservice.application.adapters.input.api.dto.RegisterRequest;
import com.taskflow.userservice.application.adapters.input.api.dto.TokenResponse;
import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.input.AuthManagerPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthManagerPort authManagerPort;

    // O Spring injeta a Porta de Entrada (que é implementada pelo AuthManagerService)
    public AuthController(AuthManagerPort authManagerPort) {
        this.authManagerPort = authManagerPort;
    }

    // --- Endpoint de Registro ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // 1. Mapeamento do DTO (Infra) para o Modelo de Domínio (Core)
        User newUser = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                // Passando a senha em texto puro para o Domínio tratar a criptografia
                .passwordHash(request.getPassword())
                .build();

        try {
            // 2. Chamada à Porta de Entrada do Domínio
            authManagerPort.registerUser(newUser);
            // 3. Retorno
            return ResponseEntity.status(201).body("Usuário registrado com sucesso.");
        } catch (RuntimeException e) {
            // Tratamento simples de exceção de domínio (ex: email já existe)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Endpoint de Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // Chamada à Porta de Entrada do Domínio
        Optional<String> tokenOptional = authManagerPort.authenticateUser(
                request.getEmail(),
                request.getPassword()
        );

        if (tokenOptional.isPresent()) {
            // Sucesso: retorna o token
            return ResponseEntity.ok(new TokenResponse(tokenOptional.get()));
        } else {
            // Falha na autenticação
            return ResponseEntity.status(401).body("Credenciais inválidas.");
        }
    }
}