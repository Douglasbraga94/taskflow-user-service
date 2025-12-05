package com.taskflow.userservice.domain.port.input;

/**
 * Port de entrada para verificar o estado inicial do sistema.
 */
public interface SystemStatusCheckerPort {

    /**
     * Verifica se o sistema já tem algum usuário registrado.
     * @return true se houver pelo menos um usuário, false caso contrário.
     */
    boolean hasAnyUserRegistered();
}