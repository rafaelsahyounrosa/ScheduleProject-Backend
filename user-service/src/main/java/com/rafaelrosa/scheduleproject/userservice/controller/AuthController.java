package com.rafaelrosa.scheduleproject.userservice.controller;

import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.service.UserService;
import com.rafaelrosa.scheduleproject.userservice.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    //TODO Melhorar retorno e incluir Company no DTO que volta como resposta
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody User user) {

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

        var authToken = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        String jwt = null;
        String role = null;
        try {
            authenticationManager.authenticate(authToken);

            //TODO checar novo modelo passando as roles ao inves de so o username
            jwt = jwtUtils.generateToken(loginRequest.username(), authToken.getAuthorities());
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
            System.out.println("Log do userDetailsService.loadUserByUsername(loginRequest.username()) em AuthController. Usuario: " + userDetails);
            role = userDetails
                    .getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");
        } catch (AuthenticationException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new LoginResponse(jwt, loginRequest.username(), role.replace("ROLE_", "")));
    }


}
