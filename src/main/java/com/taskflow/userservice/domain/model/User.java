package com.taskflow.userservice.domain.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {

    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private boolean active;
    private Role role;

    public void activate(){
        this.active = true;
    }
}
