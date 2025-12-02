package com.taskflow.userservice.domain.port.output;

import com.taskflow.userservice.domain.model.User;

public interface JwtGeneratorPort {
    /**
     * Gera um token JWT contendo as informações essenciais do usuário (ID e Papel/Role).
     * @param user o objeto User autenticado.
     * @return o token JWT como uma String.
     */
    String generateToken(User user);
}
