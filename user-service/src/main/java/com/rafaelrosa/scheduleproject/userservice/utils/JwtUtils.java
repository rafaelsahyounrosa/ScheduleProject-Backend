package com.rafaelrosa.scheduleproject.userservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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

    private UserDetails userDetails;

    private SecretKey signingKey() {
        // Se a secret estiver Base64 (recomendado):
        byte[] keyBytes =  SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);

        // Se preferir usar string "crua", troque por:
        // return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        //Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder().
                setSubject(username).
                claim("roles", roles).
                setIssuedAt(Date.from(now)).
                setExpiration(Date.from(now.plusMillis(EXPIRATION_TIME))).
                signWith(signingKey(), Jwts.SIG.HS256).
                compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        //TODO remover logs
        System.out.println("[JWT] validateToken() log. username != null =" + username != null);
        System.out.println("[JWT] validateToken() log. username = " + username);
        System.out.println("[JWT] validateToken() log. username.equals(expectedUsername) = " + username.equals(userDetails));
        System.out.println("[JWT] validateToken() log. expectedUsername = " + userDetails);
        System.out.println("[JWT] validateToken() log. isTokenExpired(token) = " + isTokenExpired(token));

        try{
            Claims claims = parseAllClaims(token);
            String sub = claims.getSubject();
            Date exp = claims.getExpiration();
            boolean subjectOk = (sub != null && sub.equals(userDetails.getUsername()));
            boolean notExpired = (exp != null || exp.after(new Date()));

            System.out.println("[JWT] sub=" + sub + " exp=" + exp + " subjectOk=" + subjectOk + " notExpired=" + notExpired);
            return subjectOk && notExpired;

        }
        catch (Exception e){
            System.out.println("[JWT] validateToken exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractUsername(String token) {
        System.out.println("[JWT] extractUsername() log. subject = " + parseAllClaims(token).getSubject());
        return parseAllClaims(token).getSubject();
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseAllClaims(token);
        return claimsResolver.apply(claims);
    }


}
