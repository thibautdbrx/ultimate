package org.ultimateam.apiultimate.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.ultimateam.apiultimate.filter.JwtFilter;
import org.ultimateam.apiultimate.service.CustomUserDetailsService;

/**
 * Configuration principale de la sécurité de l'application via Spring Security.
 *
 * <p>Cette classe définit les politiques d'accès aux ressources, la gestion des rôles
 * (ADMIN, ARBITRE, JOUEUR), l'encodage des mots de passe et l'intégration du filtre JWT.</p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;

    @Value("${app.security.enabled}")
    private boolean securityEnabled;

    /**
     * Définit l'algorithme de hachage utilisé pour les mots de passe.
     *
     * @return un {@link BCryptPasswordEncoder} pour sécuriser les identifiants
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expose le gestionnaire d'authentification standard de Spring Security.
     *
     * @param config la configuration d'authentification fournie par Spring
     * @return l'{@link AuthenticationManager} configuré
     * @throws Exception en cas d'erreur de récupération du manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure le fournisseur d'authentification utilisant {@link CustomUserDetailsService}.
     *
     * @return un {@link AuthenticationProvider} liant le service utilisateur et l'encodeur
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Définit la chaîne de filtres de sécurité et les règles d'autorisation par endpoint.
     *
     * <p>La configuration gère deux modes :
     * 1. Mode Développement : Si {@code securityEnabled} est faux, tout est autorisé.
     * 2. Mode Production : Accès public pour la lecture (GET) et la doc,
     * accès restreint par rôle pour les modifications.</p>
     *
     * @param http le constructeur de sécurité HTTP
     * @return la {@link SecurityFilterChain} configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // --- MODE DEV (Sécurité désactivée) ---
        if (!securityEnabled) {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .build();
        }

        // --- MODE PROD (Sécurité active) ---
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC : Authentification et Documentation
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/documentation/**"
                        ).permitAll()

                        // 2. PUBLIC (LECTURE SEULE) : On autorise les GET pour tout le monde
                        // C'est ici que j'ai ajouté "/api/participation/**" pour corriger ton erreur 403
                        .requestMatchers(HttpMethod.GET,
                                "/api/participation/**", // <--- LE FIX EST ICI
                                "/api/equipe/**",
                                "/api/competition/**",
                                "/api/joueur/**",
                                "/api/match/**",
                                "/api/terrain/**",
                                "/api/classement/**",
                                "/api/action-match/**",
                                "/api/files/**" // Pour afficher les images de profil
                        ).permitAll()

                        // 3. JOUEUR CONNECTÉ (Actions courantes)
                        // Upload image, rejoindre équipe, modifier son profil (PATCH)
                        .requestMatchers("/api/files/upload").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/joueur/request/**").hasAuthority("ROLE_VISITEUR")
                        .requestMatchers(HttpMethod.PATCH, "/api/joueur/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_VISITEUR")
                        .requestMatchers(HttpMethod.PATCH, "/api/joueur/request/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_VISITEUR")

                        // 4. ARBITRE & ADMIN (Gestion Matchs)
                        .requestMatchers(
                                "/api/match/**",
                                "/api/action-match/**"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_ARBITRE")

                        // 5. ADMIN (Gestion Structurelle : Création/Suppression)
                        // Attention : On bloque POST/PUT/DELETE par défaut pour l'Admin,
                        // SAUF ce qui a été autorisé explicitement au-dessus pour les joueurs/arbitres.
                        .requestMatchers(HttpMethod.POST,
                                "/api/equipe/**",
                                "/api/competition/**",
                                "/api/terrain/**",
                                "/api/joueur/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/equipe/**",
                                "/api/competition/**",
                                "/api/joueur/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/**").hasAuthority("ROLE_ADMIN")

                        // 6. Le reste doit être authentifié par défaut
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}