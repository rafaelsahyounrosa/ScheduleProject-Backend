package com.rafaelrosa.commonsecurity;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    //private final @Nullable UserDetailsService uds;
    private final SecurityDefautls defautls;

    public JwtAuthenticationFilter(JwtUtils jwtUtils /*, @Autowired(required = false) UserDetailsService uds*/, SecurityDefautls defautls) {
        this.jwtUtils = jwtUtils;
        //this.uds = uds;
        this.defautls = defautls;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return defautls.isPermitted(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
        System.out.println("[JWT] path=" + request.getServletPath());
        String h = request.getHeader("Authorization");

        if(h == null || !h.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = h.substring(7);

        try{

            String username = jwtUtils.extractUsername(token);
            System.out.println("[JWT] username=" + username);

            // 1) Valida assinatura/expiração
            if(!jwtUtils.isSignatureAndTimeValid(token)){
                System.out.println("[JWT] token inválido/expirado");
                filterChain.doFilter(request, response);
                return;
            }

            //Tenta extrair roles da claim
            Collection<? extends GrantedAuthority> authorities = jwtUtils.extractAuthorities(token);

            //Monta o principal sem precisar do UDS
            var principal = new User(username, "", authorities);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            System.out.println("[JWT] auth SET authorities=" + authorities);

        }catch (Exception e){
            //TODO remover log
            // log para debugar; não derrube a request aqui
            System.out.println("[JWT] exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            //filterChain.doFilter(request, response);
        }
        filterChain.doFilter(request, response);
    }
}
