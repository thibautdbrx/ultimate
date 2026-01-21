package org.ultimateam.apiultimate.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration globale du Cross-Origin Resource Sharing (CORS) pour l'application.
 *
 * <p>Cette classe définit les règles de sécurité permettant à des ressources provenant
 * d'origines différentes (domaines, ports ou protocoles) de communiquer avec l'API.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure les règles de mapping CORS pour l'ensemble des points d'entrée de l'API.
     *
     * <p>La configuration actuelle autorise toutes les origines, toutes les méthodes HTTP
     * et tous les en-têtes pour tous les chemins ("/**").</p>
     *
     * @param registry le registre permettant d'enregistrer les configurations de mapping CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}