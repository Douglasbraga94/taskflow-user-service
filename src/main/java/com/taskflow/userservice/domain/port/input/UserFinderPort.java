package com.taskflow.userservice.domain.port.input;

import com.taskflow.userservice.domain.model.User;
import java.util.Optional;

public interface UserFinderPort {

    /**
     * Busca um usuário pelo seu email de identificação.
     * Este metodo e usado para buscar o perfil do usuário logado (usando o email do JWT).
     * @param email o e-mail do usuário.
     * @return O objeto User de Domínio, se encontrado.
     */
    Optional<User> findUserByEmail(String email);
}