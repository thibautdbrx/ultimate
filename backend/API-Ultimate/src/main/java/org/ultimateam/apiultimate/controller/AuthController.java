package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.configuration.JwtUtils;

// 1. Importez vos nouveaux DTOs (records)
import org.ultimateam.apiultimate.DTO.AuthResponse;
import org.ultimateam.apiultimate.DTO.LoginRequest;
import org.ultimateam.apiultimate.DTO.RegisterRequest;

import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.User;
import org.ultimateam.apiultimate.repository.UserRepository;
import org.ultimateam.apiultimate.service.JoueurService;

/**
 * Contrôleur REST pour la gestion de l'authentification des utilisateurs.
 *
 * Ce contrôleur expose des endpoints pour l'inscription et la connexion des utilisateurs,
 * en utilisant JWT pour la génération des tokens d'authentification.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "Endpoints pour l'authentification")
public class AuthController {

    /** Repository pour accéder aux données des utilisateurs. */
    private final UserRepository userRepository;
    /** Service pour encoder les mots de passe. */
    private final PasswordEncoder passwordEncoder;
    /** Utilitaire pour générer et manipuler les tokens JWT. */
    private final JwtUtils jwtUtils;
    /** Gestionnaire d'authentification Spring Security. */
    private final AuthenticationManager authenticationManager;
    /** Service pour gérer les joueurs. */
    private final JoueurService joueurService;

    /**
     * Inscrit un nouvel utilisateur et crée un joueur associé.
     *
     * Ce endpoint :
     *   - Vérifie que l'email n'est pas déjà utilisé.
     *   - Crée un nouvel utilisateur avec le rôle {@link User.Role#ROLE_VISITEUR} par défaut.
     *   - Crypte le mot de passe avant de le sauvegarder.
     *   - Crée un joueur associé à l'utilisateur.
     *   - Génère un token JWT pour l'utilisateur.
     *
     * @param request Objet {@link RegisterRequest} contenant les informations d'inscription (email, mot de passe, prénom, nom, genre).
     * @return Une {@link ResponseEntity} contenant :
     *         - Un objet {@link AuthResponse} avec le token JWT et le type "Bearer" en cas de succès (HTTP 200).
     *         - Un message d'erreur si l'email est déjà utilisé (HTTP 400).
     */
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
        Joueur joueur = new Joueur();
        joueur.setPrenomJoueur(request.prenom());
        joueur.setNomJoueur(request.nom());
        joueur.setGenre(request.genre());
        joueurService.addJoueur(joueur);
        String token = jwtUtils.generateToken(
                newUser.getEmail(),
                newUser.getRole(),
                joueur.getIdJoueur()
        ); // crée le token pour l'utilisateur
        newUser.setJoueur(joueur);
        userRepository.save(newUser);
        return ResponseEntity.ok(new AuthResponse(token, "Bearer")); // 200 ok avec le token
    }

    /**
     * Authentifie un utilisateur existant et génère un token JWT.
     *
     * Ce endpoint :
     *   - Vérifie les informations d'authentification (email et mot de passe).
     *   - Génère un token JWT si l'authentification réussit.
     *   - Inclut l'identifiant du joueur dans le token si l'utilisateur est un joueur ou un arbitre.
     *
     * @param request Objet {@link LoginRequest} contenant l'email et le mot de passe.
     * @return Une {@link ResponseEntity} contenant :
     *         - Un objet {@link AuthResponse} avec le token JWT et le type "Bearer" en cas de succès (HTTP 200).
     *         - Un message d'erreur si l'authentification échoue (HTTP 401).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            ); // permet de verifier s'il existe sinon ca fait une authentication exception

            User authenticatedUser = (User) authentication.getPrincipal();
            Long joueurId = null;
            // On met joueurId seulement pour les joueurs, null pour les admins
            if (authenticatedUser.getRole() == User.Role.ROLE_VISITEUR || authenticatedUser.getRole() == User.Role.ROLE_ARBITRE) {
                if (authenticatedUser.getJoueur() != null) {
                    joueurId = authenticatedUser.getJoueur().getIdJoueur();
                }
            }
            String token = jwtUtils.generateToken(request.email(), authenticatedUser.getRole(), joueurId); // génère le token

            return ResponseEntity.ok(new AuthResponse(token, "Bearer")); // renvoie le token de meme

        } catch (AuthenticationException e) {
            log.error("Échec de l'authentification pour l'email: {}", request.email(), e); //erreur dans la console
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect."); // 401 non autorisé
        }
    }
}