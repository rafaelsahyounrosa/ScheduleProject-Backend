package com.rafaelrosa.commonsecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtils {

    private final SecretKey key;
    private final long clockSkewSeconds;

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
}
