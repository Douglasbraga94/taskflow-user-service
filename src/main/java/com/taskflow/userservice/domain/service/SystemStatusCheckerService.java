package com.taskflow.userservice.domain.service;

import com.taskflow.userservice.domain.port.input.SystemStatusCheckerPort;
import com.taskflow.userservice.domain.port.output.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class SystemStatusCheckerService implements SystemStatusCheckerPort {

    private final UserRepositoryPort userRepositoryPort;

    public SystemStatusCheckerService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public boolean hasAnyUserRegistered() {
        return userRepositoryPort.existsAnyUser();
    }
}