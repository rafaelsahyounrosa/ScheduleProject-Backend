package com.rafaelrosa.scheduleproject.userservice.controller;


import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersControllers {

    private final UserService userService;

    public UsersControllers(UserService userService) {
        this.userService = userService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserView>> getUsers(@RequestParam(required = false) String search,
                                                   Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(search, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserByIdd(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByUsername(id));
    }
}
