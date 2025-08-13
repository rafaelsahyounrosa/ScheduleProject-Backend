package com.rafaelrosa.scheduleproject.userservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

       //TODO Remover rotas de exemplo abaixo
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register").permitAll() // Endpoints públicos
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Apenas ADMIN pode acessar
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // USER e ADMIN podem acessar
                        .requestMatchers("/company/**").authenticated() // Qualquer usuário autenticado pode acessar
                        .anyRequest().authenticated() // Todas as outras rotas exigem autenticação
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
