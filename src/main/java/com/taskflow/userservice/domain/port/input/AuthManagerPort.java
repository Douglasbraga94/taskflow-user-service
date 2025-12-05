package com.taskflow.userservice.domain.port.input;

import com.taskflow.userservice.domain.model.User;

import java.util.Optional;

public interface AuthManagerPort {

    /**
     * Processa a criação de um novo usuário comum (MEMBER).
     * @param user o objeto User contendo os dados de registro (nome, email, senha).
     * @return o usuário recém-criado.
     */
    User registerUser(User user);

    /**
     * Tenta registrar o primeiro usuário do sistema, atribuindo-lhe a role ADMIN.
     * Esta operação só é permitida se não houver usuários cadastrados.
     * @param user o objeto User contendo os dados de registro.
     * @return o usuário ADMIN recém-criado.
     */
    User registerFirstAdminUser(User user);

    /**
     * Autentica um usuário e gera um token JWT.
     * @param email o e-mail do usuário.
     * @param rawPassword a senha em texto puro fornecida pelo usuário.
     * @return um Optional contendo o JWT se a autenticação for bem-sucedida.
     */
    Optional<String> authenticateUser(String email, String rawPassword);
}