package org.ultimateam.apiultimate.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.ultimateam.apiultimate.model.User;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Composant utilitaire pour la gestion des JSON Web Tokens (JWT).
 *
 * <p>Cette classe fournit les outils nécessaires pour générer, parser et valider les tokens JWT.
 * Elle permet notamment l'extraction des informations (Claims) telles que le nom d'utilisateur,
 * le rôle ou l'identifiant du joueur associé.</p>
 */
@Component
public class JwtUtils {

    //récupere les valeurs depuois application.properties
    @Value("${app.secret-key}")
    private String secretKey; //en base64
    @Value("${app.expiration-time}")
    private long expirationTime;

    /**
     * Génère un token JWT pour un utilisateur spécifique.
     *
     * @param username l'identifiant (email) de l'utilisateur
     * @param role     le rôle de l'utilisateur (ADMIN, USER, etc.)
     * @param joueurId l'identifiant technique du joueur à inclure dans les claims
     * @return une chaîne de caractères représentant le token JWT compacté
     */
    public String generateToken(String username, User.Role role, Long joueurId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("joueurId", joueurId); // ajoute l'id du joueur au token
        return createToken(claims, username, role);
    }

    /**
     * Construit le token avec les informations fournies, la date d'expiration et la signature.
     *
     * @param claims   les données personnalisées à inclure
     * @param username le sujet du token (subject)
     * @param role     le rôle à inclure dans les claims
     * @return le token JWT finalisé
     */
    private String createToken(Map<String, Object> claims, String username, User.Role role) {
        return Jwts.builder() //construit le token
                .setClaims(claims)
                .setSubject(username) //email de user dans le token
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis())) // date de création
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // d'expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256) //signe avec la cle
                .compact(); // renvoie le token
    }

    /**
     * Extrait l'identifiant du joueur contenu dans le token.
     *
     * @param token le jeton JWT
     * @return l'identifiant du joueur sous forme de Long
     */
    public Long extractJoueurId(String token) {
        return extractClaim(token, claims -> Long.valueOf(claims.get("joueurId").toString()));
    }
    /**
     * Décode la clé secrète base64 et génère la clé de signature HMAC.
     *
     * @return l'objet {@link Key} utilisé pour signer ou vérifier le token
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //transforme la clé secrete en base64 en une clé crypté
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valide si le token appartient à l'utilisateur et n'est pas expiré.
     *
     * @param token       le jeton JWT à valider
     * @param userDetails les détails de l'utilisateur chargé depuis la base
     * @return {@code true} si le token est valide, {@code false} sinon
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return(username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // si email token = email user & token pas expiré
    }

    /**
     * Vérifie si la date d'expiration du token est dépassée.
     *
     * @param token le jeton JWT
     * @return {@code true} si le token est expiré
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait le nom d'utilisateur (subject) du token.
     *
     * @param token le jeton JWT
     * @return l'email/username contenu dans le token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait le rôle de l'utilisateur contenu dans le token.
     *
     * @param token le jeton JWT
     * @return le {@link User.Role} extrait
     */
    public User.Role extractRole(String token) { return extractClaim(token, claims -> User.Role.valueOf(claims.get("roles").toString()));}

    /**
     * Extrait la date d'expiration du token.
     *
     * @param token le jeton JWT
     * @return la date d'expiration {@link Date}
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Méthode générique pour extraire une information spécifique (Claim) du token.
     *
     * @param token          le jeton JWT
     * @param claimsResolver la fonction de résolution pour extraire la donnée souhaitée
     * @param <T>            le type de la donnée à extraire
     * @return la donnée extraite
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    /**
     * Analyse le token et récupère l'ensemble de son contenu après vérification de la signature.
     *
     * @param token le jeton JWT
     * @return l'objet {@link Claims} contenant toutes les données du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()// analyse le token
                .setSigningKey(getSignKey()) //verifie la signature avec la clé
                .build()
                .parseClaimsJws(token) //si faux ca plante
                .getBody(); // sinon renvoie les info du token
    }


}
