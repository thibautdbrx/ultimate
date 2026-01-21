package org.ultimateam.apiultimate.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie; // <-- N'oublie pas cet import !
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.ultimateam.apiultimate.configuration.JwtUtils;
import org.ultimateam.apiultimate.service.CustomUserDetailsService;

import java.io.IOException;


/**
 * Filtre de sécurité pour l'authentification basée sur les jetons JWT.
 *
 * <p>Ce filtre intercepte chaque requête HTTP entrante pour extraire un token JWT
 * depuis les en-têtes ou les cookies. Il valide ensuite l'identité de l'utilisateur
 * et configure le contexte de sécurité de Spring Security.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Analyse la requête pour authentifier l'utilisateur via un token JWT.
     *
     * <p>Le processus suit quatre étapes clés :
     * 1. Recherche du token dans le header "Authorization".
     * 2. Recherche du token dans les cookies si le header est absent.
     * 3. Extraction du nom d'utilisateur depuis le jeton.
     * 4. Validation et injection de l'authentification dans le contexte de sécurité.</p>
     *
     * @param request     L'objet {@link HttpServletRequest} contenant les données de la requête.
     * @param response    L'objet {@link HttpServletResponse} pour la gestion de la réponse.
     * @param filterChain La chaîne de filtres {@link FilterChain} à poursuivre.
     * @throws ServletException en cas d'erreur lors du traitement de la servlet.
     * @throws IOException      en cas d'erreur d'entrée/sortie.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = null;
        String username = null;

        // 1. Essayer de récupérer le token via le Header "Authorization" (Standard API)
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        // 2. Si pas trouvé dans le Header, essayer via les Cookies (Navigateur)
        // C'est cette partie qui manquait pour que ton POST fonctionne !
        if (jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // 3. Si on a trouvé un token (soit dans le header, soit dans le cookie), on tente l'extraction
        if (jwt != null) {
            try {
                username = jwtUtils.extractUsername(jwt);
            } catch (Exception e) {
                // Token invalide ou expiré, on ignore (l'utilisateur restera non authentifié)
            }
        }

        // 4. Authentification standard Spring Security
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}