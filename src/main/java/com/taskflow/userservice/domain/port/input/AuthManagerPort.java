package com.taskflow.userservice.domain.port.input;

import com.taskflow.userservice.domain.model.User;

import java.util.Optional;

public interface AuthManagerPort {

    /**
     * Processa a criação de um novo usuário.
     * @param user o objeto User contendo os dados de registro (nome, email, senha).
     * @return o usuário recém-criado.
     */
    User registerUser(User user);

    /**
     * Autentica um usuário e gera um token JWT.
     * @param email o e-mail do usuário.
     * @param rawPassword a senha em texto puro fornecida pelo usuário.
     * @return um Optional contendo o JWT se a autenticação for bem-sucedida.
     */
    Optional<String> authenticateUser(String email, String rawPassword);
}
