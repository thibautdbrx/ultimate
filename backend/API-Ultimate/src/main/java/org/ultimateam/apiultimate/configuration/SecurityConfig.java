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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
//@EnableMethodSecurity // Active la sécurité via annotations dans les contrôleurs (@PreAuthorize)
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

        // --- MODE DEV : Si app.security.enabled=false ---
        if (!securityEnabled) {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    // AJOUT ICI : Même en mode dev, H2 a besoin des frames
                    .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .build();
        }

        // --- MODE PROD : Règles de sécurité ---
        return http
                .csrf(csrf -> csrf
                        // AJOUT ICI : On désactive le CSRF spécifiquement pour H2
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable()
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC : Authentification, Swagger, Fichiers ET H2
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/documentation/**",
                                "/api/files/**",
                                "/h2-console/**" // AJOUT ICI : Autorise l'accès à la console
                        ).permitAll()

                        // ... reste de tes règles existantes (GET, PATCH, etc.)
                        .requestMatchers(HttpMethod.GET, "/api/participation/**", "/api/equipe/**", "/api/competition/**", "/api/joueur/**", "/api/match/**", "/api/terrain/**", "/api/classement/**", "/api/action-match/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/joueur/{idJoueur}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/upload/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/joueur/request/{idJoueur}/equipe/{idEquipe}").permitAll()
                        .requestMatchers("/api/match/**", "/api/action-match/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                // AJOUT ICI : Autoriser les frames pour éviter l'erreur "X-Frame-Options"
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}