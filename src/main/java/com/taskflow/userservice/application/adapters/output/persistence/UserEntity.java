package com.taskflow.userservice.application.adapters.output.persistence;

import com.taskflow.userservice.domain.model.Role; // Usa o enum do Domínio
import jakarta.persistence.*; // Anotações do JPA
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Representa a entidade de banco de dados (Infraestrutura).
 * Mapeia a tabela 'users' no PostgreSQL.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String name;

    private boolean active;

    // Mapeia o enum Role para ser salvo como string no banco de dados
    @Enumerated(EnumType.STRING)
    private Role role;
}