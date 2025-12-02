package com.taskflow.userservice.application.adapters.input.api.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data // Gera Getters, Setters, toString, etc.
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password; // Atenção: Senha em texto puro no DTO
}