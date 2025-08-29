package com.rafaelrosa.scheduleproject.userservice.controller;

import com.rafaelrosa.scheduleproject.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/debug")
class AuthDebugController {
    private final PasswordEncoder encoder;
    private final UserRepository repo;
    AuthDebugController(PasswordEncoder encoder, UserRepository repo) {
        this.encoder = encoder; this.repo = repo;
    }

    @GetMapping("/check")
    public String check(@RequestParam String username, @RequestParam String raw) {
        var u = repo.findByUsername(username).orElseThrow();
        return "stored=" + u.getPassword()
                + " / matches=" + encoder.matches(raw, u.getPassword());
    }
}

