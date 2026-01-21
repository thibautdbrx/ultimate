package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.AuthResponse;
import org.ultimateam.apiultimate.DTO.LoginRequest;
import org.ultimateam.apiultimate.DTO.RegisterRequest;
import org.ultimateam.apiultimate.configuration.JwtUtils;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.User;
import org.ultimateam.apiultimate.repository.UserRepository;
import org.ultimateam.apiultimate.service.JoueurService;

/**
 * Contrôleur REST pour la gestion de l'authentification des utilisateurs.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "Endpoints pour l'inscription et la connexion")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final JoueurService joueurService;

    /**
     * Inscrit un nouvel utilisateur et crée un joueur associé.
     *
     * @param request Objet {@link RegisterRequest} contenant les informations d'inscription.
     * @return Une {@link ResponseEntity} avec le token JWT ou une erreur.
     */
    @Operation(summary = "Inscription", description = "Crée un nouvel utilisateur (Visiteur) et son profil joueur.")
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()) != null) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(User.Role.ROLE_VISITEUR);

        userRepository.save(newUser);

        Joueur joueur = new Joueur();
        joueur.setPrenomJoueur(request.prenom());
        joueur.setNomJoueur(request.nom());
        joueur.setGenre(request.genre());
        joueurService.addJoueur(joueur);

        String token = jwtUtils.generateToken(
                newUser.getEmail(),
                newUser.getRole(),
                joueur.getIdJoueur()
        );

        newUser.setJoueur(joueur);
        userRepository.save(newUser);

        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

    /**
     * Authentifie un utilisateur existant et génère un token JWT.
     *
     * @param request Objet {@link LoginRequest} contenant l'email et le mot de passe.
     * @return Une {@link ResponseEntity} avec le token JWT ou une erreur 401.
     */
    @Operation(summary = "Connexion", description = "Authentifie l'utilisateur et renvoie un token JWT.")
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            User authenticatedUser = (User) authentication.getPrincipal();
            Long joueurId = null;

            if (authenticatedUser.getRole() == User.Role.ROLE_VISITEUR) {
                if (authenticatedUser.getJoueur() != null) {
                    joueurId = authenticatedUser.getJoueur().getIdJoueur();
                }
            }

            String token = jwtUtils.generateToken(request.email(), authenticatedUser.getRole(), joueurId);

            return ResponseEntity.ok(new AuthResponse(token, "Bearer"));

        } catch (AuthenticationException e) {
            log.error("Échec de l'authentification pour l'email: {}", request.email(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect.");
        }
    }
}