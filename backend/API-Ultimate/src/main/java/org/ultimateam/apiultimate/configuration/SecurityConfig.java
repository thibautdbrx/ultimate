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
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;

    @Value("${app.security.enabled}")
    private boolean securityEnabled;


    @Bean //outil pour crypter les mdp
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

        // --- MODE OUVERT (DEV)---
        if (!securityEnabled) {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) //tout le monde est accepté
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .build();
        }

        // --- MODE SÉCURISÉ ---
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()

                                .requestMatchers(HttpMethod.GET,
                                        "/api/equipe/**",
                                        "/api/competition/**",
                                        "/api/joueur/**",
                                        "/api/classement/**",
                                        "/documentation/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/api/match/**").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/api/equipe/**",
                                        "/api/competition/**",
                                        "/api/joueur/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT,
                                        "/api/equipe/**",
                                        "/api/competition/**",
                                        "/api/joueur/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/equipe/**",
                                        "/api/competition/**",
                                        "/api/joueur/**",
                                        "/api/classement/**").hasAuthority("ROLE_ADMIN")

                                .requestMatchers(HttpMethod.POST,
                                        "/api/match/**").hasAnyAuthority("ROLE_ADMIN","ROLE_ARBITRE")
                                .requestMatchers(HttpMethod.PUT,
                                        "/api/match/**").hasAnyAuthority("ROLE_ADMIN","ROLE_ARBITRE")
                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/match/**").hasAnyAuthority("ROLE_ADMIN","ROLE_ARBITRE")

                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
