package com.taskflow.userservice.application.adapters.input.api.dto;

/**
 * DTO de resposta para o login (retorna o JWT).
 */
public record TokenResponse(
        String token,
        String tokenType
) {
    public TokenResponse(String token) {
        this(token, "Bearer");
    }
}