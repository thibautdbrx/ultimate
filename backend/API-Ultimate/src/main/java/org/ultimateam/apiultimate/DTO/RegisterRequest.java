package org.ultimateam.apiultimate.DTO;

public record RegisterRequest(String email, String password, String prenom, String nom, GenreJoueur genre) {
}

/* pas mette le role dedans */
