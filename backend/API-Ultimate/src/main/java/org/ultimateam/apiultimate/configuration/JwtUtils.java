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

@Component
public class JwtUtils {

    //récupere les valeurs depuois application.properties
    @Value("${app.secret-key}")
    private String secretKey; //en base64
    @Value("${app.expiration-time}")
    private long expirationTime;

    public String generateToken(String username, User.Role role) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, role);
    }

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

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //transforme la clé secrete en base64 en une clé crypté
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return(username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // si email token = email user & token pas expiré
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public User.Role extractRole(String token) { return extractClaim(token, claims -> User.Role.valueOf(claims.get("roles").toString()));}

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()// analyse le token
                .setSigningKey(getSignKey()) //verifie la signature avec la clé
                .build()
                .parseClaimsJws(token) //si faux ca plante
                .getBody(); // sinon renvoie les info du token
    }


}
