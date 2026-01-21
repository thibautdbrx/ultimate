package org.ultimateam.apiultimate.DTO;

/**
 * DTO pour les requêtes de connexion d'un utilisateur.
 *
 * Ce record est utilisé pour transmettre les informations d'authentification
 * (email et mot de passe) lors d'une tentative de connexion.
 * Il est utilisé par l'endpoint {@link org.ultimateam.apiultimate.controller.AuthController#login(LoginRequest)}.
 *
 * @param email    Adresse email de l'utilisateur.
 *
 * @param password Mot de passe de l'utilisateur en texte clair.
 * @see org.ultimateam.apiultimate.controller.AuthController#login(LoginRequest)
 * @see org.ultimateam.apiultimate.DTO.AuthResponse
 */

public record LoginRequest(String email,String password) {

}
