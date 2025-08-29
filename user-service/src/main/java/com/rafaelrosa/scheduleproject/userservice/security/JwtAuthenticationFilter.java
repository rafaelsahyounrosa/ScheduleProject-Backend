package com.rafaelrosa.scheduleproject.userservice.security;

import com.rafaelrosa.scheduleproject.userservice.service.AppUserDetailsService;
import com.rafaelrosa.scheduleproject.userservice.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, AppUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            // sem header -> segue anônimo
            System.out.println("[JWT] skip: sem Authorization. path=" + path);
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);
        String username = null;

        try{
            username = jwtUtils.extractUsername(token);
            // LOG CHAVE 1
            System.out.println("[JWT] path=" + path + " username=" + username);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                boolean isValid = jwtUtils.validateToken(token, userDetails);
                System.out.println("[JWT] validateToken=" + isValid
                        + " sub=" + username
                        + " userDetails.username=" + userDetails.getUsername());

                if(isValid){
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    // LOG CHAVE 3
                    System.out.println("[JWT] authentication SET. authorities=" + userDetails.getAuthorities());
                }
                else {
                    // LOG CHAVE 4
                    System.out.println("[JWT] authentication NOT set (validateToken=false).");
                }
            }

        }catch (Exception e){
            // Qualquer exceção na extração/validação -> segue como anônimo
            System.out.println("[JWT] exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    //TODO validar se o path auth pode ficar sem autenticação. Acredito que nao pois somente admiins poderao cadastrar
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // pule autenticação para rotas públicas
        return path.startsWith("/auth/") || path.startsWith("/actuator/");
    }

}
