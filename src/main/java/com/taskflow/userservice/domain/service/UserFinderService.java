package com.taskflow.userservice.domain.service;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.input.UserFinderPort;
import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserFinderService implements UserFinderPort {

    private final UserRepositoryPort userRepositoryPort;

    public UserFinderService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Implementa a busca pelo e-mail (que vem do JWT).
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepositoryPort.findByEmail(email);
    }
}