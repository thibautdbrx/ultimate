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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;

    @Value("${app.security.enabled}")
    private boolean securityEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

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
                        .requestMatchers(HttpMethod.POST, "/api/joueur/request/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/joueur/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/joueur/request/**").authenticated()

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