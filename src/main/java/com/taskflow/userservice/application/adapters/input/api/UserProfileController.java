package com.taskflow.userservice.application.adapters.input.api;

import com.taskflow.userservice.application.adapters.input.api.dto.UserProfileResponse;
import com.taskflow.userservice.domain.model.User;
import com.taskflow.userservice.domain.port.input.UserFinderPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserProfileController {

    private final UserFinderPort userFinderPort;

    public UserProfileController(UserFinderPort userFinderPort) {
        this.userFinderPort = userFinderPort;
    }

    /**
     * Endpoint protegido para buscar o perfil do usuário logado.
     * Requer qualquer token JWT válido (anyRequest().authenticated()).
     */
    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUserProfile() {

        // 1. Obter o objeto de autenticação do contexto de segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // Este caso não deve ocorrer se a rota estiver configurada como .authenticated(), mas é uma salvaguarda.
            return ResponseEntity.status(401).body("Não autenticado.");
        }

        // O principal (Principal) é o email do usuário, conforme definido no JwtAuthenticationFilter
        String userEmail = authentication.getName();

        // 2. Buscar o perfil no domínio
        Optional<User> userOptional = userFinderPort.findUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Perfil de usuário não encontrado.");
        }

        // 3. Retornar o DTO de resposta (seguro)
        UserProfileResponse response = UserProfileResponse.fromDomain(userOptional.get());
        return ResponseEntity.ok(response);
    }
}