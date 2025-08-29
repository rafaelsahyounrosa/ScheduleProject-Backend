package com.rafaelrosa.scheduleproject.userservice.controller;


import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersControllers {

    private final UserService userService;

    public UsersControllers(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }
}
