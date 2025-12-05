package com.taskflow.userservice.application.adapters.input.api.dto;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.model.Role;

/**
 * DTO de resposta para dados de perfil (seguro, sem passwordHash).
 */
public record UserProfileResponse(
        Long id,
        String name,
        String email,
        Role role
) {
    public static UserProfileResponse fromDomain(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}