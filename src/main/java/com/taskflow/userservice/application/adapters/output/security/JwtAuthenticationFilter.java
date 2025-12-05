package com.taskflow.userservice.application.adapters.output.security;

import com.taskflow.userservice.application.adapters.output.security.JwtTokenAdapter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenAdapter jwtTokenAdapter;

    // Lista de endpoints públicos que o filtro JWT DEVE IGNORAR completamente.
    // Usamos o String URI aqui para simplificar a correspondência no shouldNotFilter.
    private final List<String> publicUris = Arrays.asList(
            "/auth/login",
            "/auth/firstUser",
            "/h2-console"
    );


    public JwtAuthenticationFilter(JwtTokenAdapter jwtTokenAdapter) {
        this.jwtTokenAdapter = jwtTokenAdapter;
    }

    /**
     * Define se o filtro deve ser aplicado para a requisição atual.
     * Retorna true (DEVE FILTRAR) se o path NÃO for público.
     * * CORREÇÃO: Usamos o URI completo e verificamos se ele começa com uma URI pública,
     * ignorando o filtro completamente para essas rotas.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUri = request.getRequestURI();

        // Verifica se a URI da requisição começa com qualquer uma das URIs públicas
        // (por exemplo, /h2-console/** começa com /h2-console)
        return publicUris.stream()
                .anyMatch(requestUri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extrai o cabeçalho Authorization
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;
        String role = null;

        // Se o cabeçalho existe, tentamos processar
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);

            // 2. Valida o token e extrai dados
            // NOTA: Se o token for inválido, validateToken retorna false e userEmail/role permanecem null
            if (jwtTokenAdapter.validateToken(jwt)) {
                userEmail = jwtTokenAdapter.getSubjectFromToken(jwt);
                role = jwtTokenAdapter.getRoleFromToken(jwt);
            }
        }

        // 3. Define a autenticação no contexto do Spring Security
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cria a autoridade (ROLE) para o Spring Security, e.g., ROLE_ADMIN, ROLE_MEMBER
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            // Cria o objeto de autenticação
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userEmail, // Principal (Nome de Usuário/Email)
                    null,      // Credenciais
                    Collections.singletonList(authority) // Autoridades (Roles)
            );

            // Define o objeto de autenticação no contexto de segurança
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 4. Continua a cadeia de filtros
        // Se userEmail for nulo, a requisição passa sem autenticação, e o SecurityConfig
        // aplica o .permitAll() ou .authenticated().
        filterChain.doFilter(request, response);
    }
}