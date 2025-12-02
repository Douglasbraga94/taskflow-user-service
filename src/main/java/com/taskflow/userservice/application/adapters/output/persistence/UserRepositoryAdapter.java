package com.taskflow.userservice.application.adapters.output.persistence;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import org.springframework.stereotype.Component; // Marca a classe como um bean do Spring
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepositoryJpa userRepositoryJpa;

    // O Spring injeta a implementação do JPA Repositório aqui
    public UserRepositoryAdapter(UserRepositoryJpa userRepositoryJpa) {
        this.userRepositoryJpa = userRepositoryJpa;
    }

    // --- Implementação do Port: save ---
    @Override
    public User save(User user) {
        // 1. Converter Domínio (User) para Entidade (UserEntity)
        UserEntity entity = toEntity(user);

        // 2. Salvar usando o JPA
        UserEntity savedEntity = userRepositoryJpa.save(entity);

        // 3. Converter Entidade salva de volta para Domínio e retornar
        return toDomain(savedEntity);
    }

    // --- Implementação do Port: findByEmail ---
    @Override
    public Optional<User> findByEmail(String email) {
        // 1. Buscar a Entidade usando o JPA
        Optional<UserEntity> entity = userRepositoryJpa.findByEmail(email);

        // 2. Mapear o resultado: se existir, converter para Domínio, senão retorna Optional.empty()
        return entity.map(this::toDomain);
    }

    // --- Implementação do Port: findById (apenas para completar a interface) ---
    @Override
    public Optional<User> findById(Long id) {
        return userRepositoryJpa.findById(id).map(this::toDomain);
    }

    // --- Métodos de Conversão (Mappers) ---

    // Converte Entidade (JPA) para Domínio (Core)
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

    // Converte Domínio (Core) para Entidade (JPA)
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