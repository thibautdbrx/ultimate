package org.ultimateam.apiultimate.DTO;

/**
 * DTO (Data Transfer Object) utilisé pour la création d'un nouveau compte utilisateur.
 * * <p>Ce record encapsule les informations nécessaires lors de la phase d'inscription.
 * Le rôle n'est pas inclus dans cette structure car il est défini par défaut ou par
 * l'administrateur pour des raisons de sécurité.</p>
 *
 * @param email    L'adresse email servant d'identifiant de connexion.
 * @param password Le mot de passe de l'utilisateur (sera haché avant stockage).
 * @param prenom   Le prénom de l'utilisateur.
 * @param nom      Le nom de famille de l'utilisateur.
 * @param genre    Le genre du joueur ({@link GenreJoueur}).
 */
public record RegisterRequest(String email, String password, String prenom, String nom, GenreJoueur genre) {
}

/* pas mettre le role dedans */
