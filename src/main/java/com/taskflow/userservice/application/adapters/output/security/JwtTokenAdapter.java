package com.taskflow.userservice.application.adapters.output.security;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.output.JwtGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // Para injetar configurações
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Adapter que implementa o Port de Geração de JWT usando a biblioteca JJWT.
 * É aqui que a lógica de infraestrutura (assinatura, tempo de expiração) vive.
 */
@Component
public class JwtTokenAdapter implements JwtGeneratorPort {

    // --- Configurações Injetadas do application.properties ---

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    // --- Método Auxiliar para Obter a Chave de Assinatura ---
    private Key getSigningKey() {
        // Decodifica a chave secreta base64 configurada no application.properties
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Implementação do Port: generateToken ---
    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getEmail()) // O assunto do token (pode ser o email ou ID do usuário)
                .claim("id", user.getId()) // Adiciona o ID do usuário como uma "claim" customizada
                .claim("role", user.getRole().name()) // Adiciona o papel do usuário
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Assina o token com a chave
                .compact();
    }
}