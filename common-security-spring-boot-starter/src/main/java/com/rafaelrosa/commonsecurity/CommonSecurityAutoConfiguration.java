package com.rafaelrosa.commonsecurity;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AutoConfiguration
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, SecurityDefautls.class})
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
public class CommonSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    JwtUtils jwtUtils(JwtProperties jwtProperties) {
        return new JwtUtils(jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwt, SecurityDefautls defautls) {

        return new JwtAuthenticationFilter(jwt, defautls);
    }

    /**
     * Cadeia de segurança padrão (stateless + tudo autenticado),
     * criada somente se o serviço NÃO definir a própria SecurityFilterChain.
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
