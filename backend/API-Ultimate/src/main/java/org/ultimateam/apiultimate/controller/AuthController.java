package org.ultimateam.apiultimate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ultimateam.apiultimate.configuration.JwtUtils;

// 1. Importez vos nouveaux DTOs (records)
import org.ultimateam.apiultimate.DTO.AuthResponse;
import org.ultimateam.apiultimate.DTO.LoginRequest;
import org.ultimateam.apiultimate.DTO.RegisterRequest;

import org.ultimateam.apiultimate.model.User;
import org.ultimateam.apiultimate.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) { // permet d'évité qu'une personne envoie un json avec comme role admin

        if (userRepository.findByEmail(request.email()) != null) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }
        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password())); //cryote le mdp

        User.Role defaultRole = User.Role.ROLE_VISITEUR;
        newUser.setRole(User.Role.ROLE_VISITEUR); // Permet de forcer le role par defaut pour pas q'un pirate s'inscrit en admin

        userRepository.save(newUser);

        String token = jwtUtils.generateToken(newUser.getEmail()); // crée le token pour l'utilisateur
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", defaultRole.name())); // 200 ok avec le token
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            ); // permet de verifier s'il existe sinon ca fait une authentication exception

            User authenticatedUser = (User) authentication.getPrincipal();
            String token = jwtUtils.generateToken(request.email()); // génère le token

            return ResponseEntity.ok(new AuthResponse(token, "Bearer", authenticatedUser.getRole().name())); // renvoie le token de meme

        } catch (AuthenticationException e) {
            log.error("Échec de l'authentification pour l'email: {}", request.email(), e); //erreur dans la console
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect."); // 401 non autorisé
        }
    }
}