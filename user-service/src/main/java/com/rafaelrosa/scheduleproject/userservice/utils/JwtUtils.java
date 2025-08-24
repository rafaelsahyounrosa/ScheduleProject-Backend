package com.rafaelrosa.scheduleproject.userservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

//    private static final String SECRET = "secret";
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;
//    TODO ver se vai funcionar com a variavel de ambiente declarada assim
    @Value("${security.jwt.secret}")
    private String SECRET;

    @Value("${security.jwt.expiration}")
    private long EXPIRATION_TIME;

    private SecretKey signingKey() {
        // Se a secret estiver Base64 (recomendado):
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);

        // Se preferir usar string "crua", troque por:
        // return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder().
                setSubject(username).
                setIssuedAt(now).
                setExpiration(expirationDate).
                signWith(signingKey()).
                compact();
    }

    public boolean validateToken(String token, UserDetails expectedUsername) {
        String username = extractUsername(token);
        return username != null && username.equals(expectedUsername) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }


}
