package com.taskflow.userservice.application.adapters.input.api;

import com.taskflow.userservice.application.adapters.input.api.dto.LoginRequest;
import com.taskflow.userservice.application.adapters.input.api.dto.RegisterRequest;
import com.taskflow.userservice.application.adapters.input.api.dto.TokenResponse;
import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.input.AuthManagerPort;
import com.taskflow.userservice.domain.port.input.SystemStatusCheckerPort; // NOVO IMPORT
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthManagerPort authManagerPort;
    private final SystemStatusCheckerPort systemStatusCheckerPort; // NOVO CAMPO

    // O Spring injeta a Porta de Entrada (AuthManagerPort) e a Porta de Verificação de Estado (SystemStatusCheckerPort)
    public AuthController(AuthManagerPort authManagerPort, SystemStatusCheckerPort systemStatusCheckerPort) {
        this.authManagerPort = authManagerPort;
        this.systemStatusCheckerPort = systemStatusCheckerPort;
    }

    // --- Endpoint de Registro (Não pode ser usado se /firstUser já foi executado, mas será validado no domínio) ---
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
            return ResponseEntity.ok(new TokenResponse(tokenOptional.get(), "Bearer")); // Retornando o TokenResponse que você utiliza
        } else {
            // Falha na autenticação
            return ResponseEntity.status(401).body("Credenciais inválidas.");
        }
    }

    // --- Endpoint de Registro ÚNICO do Primeiro ADMIN ---
    @PostMapping("/firstUser")
    public ResponseEntity<?> registerFirstUser(@Valid @RequestBody RegisterRequest request) {

        // 1. VERIFICAÇÃO CRÍTICA DE EXECUÇÃO ÚNICA: Se já houver um usuário, retorna 409 CONFLICT.
        if (systemStatusCheckerPort.hasAnyUserRegistered()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("O primeiro usuário ADMIN já foi registrado. Esta operação não é permitida novamente.");
        }

        // Mapeamento do DTO para o Modelo de Domínio
        User newUser = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .passwordHash(request.getPassword())
                .build();

        try {
            // 2. Chamada ao caso de uso que cria o primeiro ADMIN
            authManagerPort.registerFirstAdminUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("Primeiro usuário ADMIN registrado com sucesso.");
        } catch (RuntimeException e) {
            // Trata se o AuthManagerService detectar um race condition
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}