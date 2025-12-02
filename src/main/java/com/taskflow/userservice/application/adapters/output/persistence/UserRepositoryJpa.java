package com.taskflow.userservice.application.adapters.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Interface simples do Spring Data JPA.
 * O Spring cria automaticamente a implementação que faz o CRUD.
 */
public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long> {

    // Spring Data JPA gera o metodo SQL automaticamente a partir do nome!
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByname(String nome);
}