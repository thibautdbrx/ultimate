package org.ultimateam.apiultimate.DTO;

public record RegisterRequest(String email, String password, String prenom, String nom, GenreJoueur genre) {
}

/* pas mettre le role dedans */
