package org.ultimateam.apiultimate.DTO;

/**
 * Réponse d'authentification contenant le token JWT et son type.
 *
 * Ce DTO est retourné par les endpoints d'authentification ({@link AuthController})
 * après une connexion ou une inscription réussie. Il permet au client de stocker
 * le token et de l'utiliser pour les requêtes authentifiées ultérieures.
 *
 * @param token Le token JWT généré pour l'utilisateur authentifié.
 *              Doit être inclus dans l'en-tête "Authorization" des requêtes suivantes.
 *
 * @param type  Le type de token, toujours "Bearer" pour une authentification JWT standard.
 *              Doit être utilisé comme préfixe dans l'en-tête "Authorization".
 */

public record AuthResponse(
        String token,
        String type) {}