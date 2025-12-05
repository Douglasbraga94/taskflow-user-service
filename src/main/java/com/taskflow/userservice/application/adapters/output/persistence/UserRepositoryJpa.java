package com.taskflow.userservice.application.adapters.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.taskflow.userservice.application.adapters.output.persistence.UserEntity;
import java.util.Optional;

/**
 * Repositório JPA que interage diretamente com o banco de dados.
 */
public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long> {

    // Usado pelo UserRepositoryAdapter
    Optional<UserEntity> findByEmail(String email);

    // O metodo count() e findById() são fornecidos por JpaRepository.
}