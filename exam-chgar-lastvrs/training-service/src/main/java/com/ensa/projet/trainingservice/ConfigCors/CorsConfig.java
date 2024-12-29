package com.ensa.projet.trainingservice.ConfigCors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // Créer une nouvelle configuration CORS
        CorsConfiguration config = new CorsConfiguration();

        // Autoriser les cookies
        config.setAllowCredentials(true);

        // Ajouter l'origine autorisée (l'adresse du frontend)
        config.addAllowedOrigin("http://localhost:3000");  // Changez cette adresse si nécessaire

        // Autoriser tous les headers
        config.addAllowedHeader("*");

        // Autoriser toutes les méthodes HTTP
        config.addAllowedMethod("*");

        // Enregistrer la configuration CORS sur toutes les URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Retourner une nouvelle instance de CorsFilter avec la configuration
        return new CorsFilter(source);
    }
}
