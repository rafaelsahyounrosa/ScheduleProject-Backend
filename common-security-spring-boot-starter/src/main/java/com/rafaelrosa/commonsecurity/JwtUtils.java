package com.rafaelrosa.commonsecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JwtUtils {

    private final SecretKey key;
    private final long clockSkewSeconds;
    @Value("${security.jwt.secret}")
    private String SECRET;
    @Value("${security.jwt.expiration}")
    private long EXPIRATION_TIME;

    public JwtUtils(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.clockSkewSeconds = props.clockSkewSeconds();
        System.out.println("[JWT] starter ready. secretLength=" + props.secret().length());

    }

    private JwtParser parser(){
        return Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(clockSkewSeconds)
                .build();
    }

    String extractUsername(String token){
        return parser()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    boolean validate(String token, UserDetails userDetails){
        try{
            Claims c = parser().parseSignedClaims(token).getPayload();
            String sub = c.getSubject();
            Date exp = c.getExpiration();
            boolean subjectOk = sub != null && sub.equals(userDetails.getUsername());
            boolean notExpired = exp == null || exp.after(new Date());

            return subjectOk && notExpired;
        }
        catch (Exception e){
            return false;
        }
    }

    boolean isSignatureAndTimeValid(String token) {
        try {
            Claims c = parser().parseSignedClaims(token).getPayload();
            Date exp = c.getExpiration();

            return exp == null || exp.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        try {
            Claims c = parser().parseSignedClaims(token).getPayload();
            Object rolesObj = c.get("roles");
            if(rolesObj instanceof Collection<?> col){

                return col.stream()
                        .map(Object::toString)
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            return List.of();
        }
        return List.of();
    }

    private SecretKey signingKey() {
        // Se a secret estiver Base64 (recomendado):
        byte[] keyBytes =  SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);

        // Se preferir usar string "crua", troque por:
        // return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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
