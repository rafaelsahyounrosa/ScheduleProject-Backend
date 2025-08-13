package com.rafaelrosa.scheduleproject.userservice.controller;

import com.rafaelrosa.scheduleproject.commonentities.UserCredentialsDTO;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.service.UserService;
import com.rafaelrosa.scheduleproject.userservice.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthController(UserService userService, JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    //TODO Remover PWD do UserDTO e usar aqui
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {

        User newUser = userService.registerUser(user);
        newUser.setPassword(null); //TODO só por segurança por hora para remover a senha do retorno da API -> Remover
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCredentialsDTO userCredentialsDTO) {

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentialsDTO.getUsername(),
                            userCredentialsDTO.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userCredentialsDTO.getUsername());
        String token = jwtUtils.generateToken(userCredentialsDTO.getUsername());
        return ResponseEntity.ok(new UserCredentialsDTO(userCredentialsDTO.getUsername(), token));
    }



}
