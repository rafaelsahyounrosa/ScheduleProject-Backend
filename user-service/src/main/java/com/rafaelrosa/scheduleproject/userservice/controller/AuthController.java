package com.rafaelrosa.scheduleproject.userservice.controller;

import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.service.UserService;
import com.rafaelrosa.scheduleproject.userservice.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    //TODO criar endpoints para company

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    //DTOs
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token, String username, String role) {}
    public record RegisterResponse(Long id, String username, String name, String email, String role, String company) {}

    public AuthController(UserService userService, JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    //TODO Remover do dominio publico. SOmente admns podem criar novos usuarios (e company_admin) -> Por hora com preAuthorize (entender necessidade apos implementa o CollaboratorController)
    //TODO Melhorar retorno e incluir Company no DTO que volta como resposta
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody User user) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("[REGISTER] principal={" + auth.getPrincipal() + "}, authorities={" + auth.getAuthorities() + "}");

        User newUser = userService.registerUser(user);
        RegisterResponse userResponse = new RegisterResponse(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getRole().toString(),
                newUser.getCompany() != null ? newUser.getCompany().getName() : null
        );


        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        log.debug("[LOGIN] attempt user={}", loginRequest.username());

        var authToken = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        String jwt;
        String role;
        String firstRole;

        try {
            var authenticated = authenticationManager.authenticate(authToken);

            // companyId para claim do JWT (sem nova consulta para cada request depois)
            var optUser = userService.findByUsername(loginRequest.username());
            Long companyId = optUser.map(u -> u.getCompany() != null ? u.getCompany().getId() : null).orElse(null);

            // novo overload com companyId
            jwt = jwtUtils.generateToken(loginRequest.username(), authenticated.getAuthorities(), companyId);

            role = authenticated.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
            log.debug("[LOGIN] authenticated user={}, authorities={}",
                    authenticated.getName(), authenticated.getAuthorities());

            //TODO checar novo modelo passando as roles ao inves de so o username
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
            System.out.println("Log do userDetailsService.loadUserByUsername(loginRequest.username()) em AuthController. Usuario: " + userDetails);

        } catch (BadCredentialsException e) {
            log.warn("[LOGIN] bad credentials for user={}", loginRequest.username());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (DisabledException e) {
            log.warn("[LOGIN] user disabled: {}", loginRequest.username(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AuthenticationException e) {
            log.error("[LOGIN] auth error for user={}: {}", loginRequest
                    .username(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new LoginResponse(jwt, loginRequest.username(), role.replace("ROLE_", "")));
    }

    //TODO endpoint para reset de senha geral somente pelo admin e outro para company_admin e seus subordinados


}
