package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Représente un utilisateur du système. Cette entité contient les informations
 * nécessaires à l'authentification (email, password) et à l'autorisation (role).
 * Elle implémente {@link UserDetails} pour l'intégration avec Spring Security.
 */

@Data
@Entity
@Table(name = "users")

public class User implements UserDetails {

    /**
     * Rôles possibles pour un utilisateur. Les valeurs sont formatées
     * pour être directement utilisables comme authorities (ex: ROLE_ADMIN).
     */
    public enum Role {ROLE_ADMIN, ROLE_ARBITRE, ROLE_VISITEUR};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "joueur_id", referencedColumnName = "idJoueur")
    private Joueur joueur;

    /**
     * Retourne la liste des autorités (roles) pour cet utilisateur.
     *
     * Contrainte / comportement :
     * - Convertit l'enum {@link Role} en {@link SimpleGrantedAuthority}.
     * - Spring Security utilise ces autorités pour le contrôle d'accès.
     *
     * @return une Collection de {@link GrantedAuthority} contenant au moins
     *         l'autorité correspondant au rôle de cet utilisateur.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // pour mettre des roles : ex: new SimpleGrantedAuthority(role.name())
        // sinon liste vide
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Retourne le mot de passe encodé (ou non) de l'utilisateur.
     * Spring Security le compare avec les informations fournies lors de l'authentification.
     *
     * @return la chaîne représentant le mot de passe de l'utilisateur.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Retourne le nom d'utilisateur utilisé pour l'authentification.
     * Ici l'email est utilisé comme identifiant unique.
     *
     * @return l'email de l'utilisateur (nom d'utilisateur).
     */
    @Override
    public String getUsername() {
        return email; // email comme nom d'utilisateur
    }

    /**
     * Indique si le compte n'est pas expiré. Si false, l'utilisateur ne peut pas
     * s'authentifier même si les informations d'identification sont valides.
     *
     * Dans cette implémentation, le compte est toujours considéré comme non expiré.
     *
     * @return true si le compte n'est pas expiré (toujours true ici).
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Indique si le compte n'est pas verrouillé. Si false, l'utilisateur est
     * considéré comme verrouillé et ne peut pas s'authentifier.
     *
     * Dans cette implémentation, le compte est toujours considéré comme non verrouillé.
     *
     * @return true si le compte n'est pas verrouillé (toujours true ici).
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indique si les informations d'identification (mot de passe) ne sont pas expirées.
     * Si false, l'utilisateur doit renouveler ses informations d'identification.
     *
     * Dans cette implémentation, les credentials sont toujours considérés comme non expirés.
     *
     * @return true si les credentials ne sont pas expirés (toujours true ici).
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indique si le compte est activé/enabled. Si false, l'utilisateur ne peut pas
     * s'authentifier même si les informations d'identification sont valides.
     *
     * Dans cette implémentation, le compte est toujours considéré comme activé.
     *
     * @return true si le compte est activé (toujours true ici).
     */
    @Override
    public boolean isEnabled() { return true; }
}
