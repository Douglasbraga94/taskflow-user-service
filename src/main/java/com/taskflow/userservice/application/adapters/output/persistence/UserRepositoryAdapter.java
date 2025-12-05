package com.taskflow.userservice.application.adapters.output.persistence;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepositoryJpa userRepositoryJpa;

    public UserRepositoryAdapter(UserRepositoryJpa userRepositoryJpa) {
        this.userRepositoryJpa = userRepositoryJpa;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = userRepositoryJpa.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> entity = userRepositoryJpa.findByEmail(email);
        return entity.map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepositoryJpa.findById(id).map(this::toDomain);
    }

    // --- MÉTODO CRÍTICO: Se o salvamento falha, este método sempre retorna false. ---
    @Override
    public boolean existsAnyUser() {
        return userRepositoryJpa.count() > 0;
    }

    // --- Métodos de Conversão (Mappers) ---

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .active(entity.isActive())
                .role(entity.getRole())
                .build();
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getName(),
                user.isActive(),
                user.getRole()
        );
    }
}