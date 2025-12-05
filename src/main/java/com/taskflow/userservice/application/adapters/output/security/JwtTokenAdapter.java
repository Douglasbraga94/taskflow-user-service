package com.taskflow.userservice.application.adapters.output.security;

import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.output.JwtGeneratorPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.JwtException;

/**
 * Adapter que implementa o Port de Geração de JWT usando a biblioteca JJWT.
 * É aqui que a lógica de infraestrutura (assinatura, tempo de expiração) vive.
 * Também contém a lógica para validar e ler o token (usado pelo filtro de segurança).
 */
@Component
public class JwtTokenAdapter implements JwtGeneratorPort {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Implementação do Port: generateToken (Único com @Override) ---
    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // --- Métodos de Segurança (NÃO TÊM @Override) ---

    /**
     * Valida a assinatura e a expiração do token.
     * @param token o token JWT.
     * @return true se o token for válido, false caso contrário.
     */
    public boolean validateToken(String token) {
        try {
            // Se o token for inválido, o método parseClaimsJws lança uma JwtException (RuntimeException).
            // O JJWT lança ExpiredJwtException se o token estiver antigo.
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            // Token expirado.
            System.err.println("JWT Token Expirado: " + ex.getMessage()); // Adicionando log para debug
        } catch (SignatureException ex) {
            // Assinatura inválida.
            System.err.println("JWT Assinatura Inválida: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            // Token malformado.
            System.err.println("JWT Token Malformado: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            // Token não suportado.
        } catch (IllegalArgumentException ex) {
            // Claims string JWT vazia.
        }
        // Captura qualquer outra exceção genérica do JWT.
        catch (JwtException ex) {
            System.err.println("JWT Erro Genérico: " + ex.getMessage());
        }
        // Se qualquer exceção for capturada (incluindo expiração), retorna false.
        return false;
    }

    /**
     * Extrai o nome de usuário (Subject) do token.
     * @param token o token JWT.
     * @return o e-mail do usuário.
     */
    public String getSubjectFromToken(String token) {
        // Se a validação falhou, este método DEVE ser chamado apenas se você não precisar
        // dos claims. No nosso filtro, ele só é chamado após validateToken retornar true.
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     * Extrai o papel (Role) do token.
     * @param token o token JWT.
     * @return o papel (Role) como String.
     */
    public String getRoleFromToken(String token) {
        // Se a validação falhou, este método DEVE ser chamado apenas se você não precisar
        // dos claims. No nosso filtro, ele só é chamado após validateToken retornar true.
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token)
                .getBody().get("role", String.class);
    }

}