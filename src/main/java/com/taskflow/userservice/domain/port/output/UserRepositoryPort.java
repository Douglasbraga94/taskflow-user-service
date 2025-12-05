package com.taskflow.userservice.domain.port.output;

import com.taskflow.userservice.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    /**
     * Salva ou atualiza um usuário no repositório.
     * @param user o objeto User a ser salvo.
     * @return o objeto User salvo (com ID, se for um novo registro).
     */
    User save(User user);

    /**
     * Busca um usuário pelo seu identificador único.
     * @param id o ID do usuário.
     * @return um Optional contendo o User, se encontrado.
     */
    Optional<User> findById(Long id);


    /**
     * Busca um usuário pelo seu endereço de e-mail (usado para login).
     * @param email o e-mail do usuário.
     * @return um Optional contendo o User, se encontrado.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se o repositório contém algum usuário.
     * @return true se houver pelo menos um usuário, false caso contrário.
     */
    boolean existsAnyUser();
}