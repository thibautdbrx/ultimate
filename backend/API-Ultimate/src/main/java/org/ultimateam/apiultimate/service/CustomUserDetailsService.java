package org.ultimateam.apiultimate.service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.User;
import org.ultimateam.apiultimate.repository.UserRepository;

/**
 * Service chargé de fournir les informations d'authentification pour Spring Security.
 *
 * <p>Ce service implémente {@link UserDetailsService} et permet de charger un
 * {@link UserDetails} (ici l'entité {@link User}) à partir d'un identifiant de connexion
 * (dans ce projet l'email).</p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Charge un utilisateur par son nom d'utilisateur (ici l'email) pour Spring Security.
     *
     * <p>La méthode recherche un {@link User} via le {@link UserRepository} en utilisant
     * l'email fourni. Si l'utilisateur n'est pas trouvé, elle lance une
     * {@link UsernameNotFoundException} afin que Spring Security puisse gérer l'échec
     * d'authentification.</p>
     *
     * @param username l'email ou le nom d'utilisateur utilisé pour la recherche
     * @return un objet {@link UserDetails} représentant l'utilisateur trouvé
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé pour l'email fourni
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Email introuvrable");
        }
        return user;
    }
}