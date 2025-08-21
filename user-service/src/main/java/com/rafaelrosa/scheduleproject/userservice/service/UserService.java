package com.rafaelrosa.scheduleproject.userservice.service;

import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.repository.UserRepository;
import com.rafaelrosa.scheduleproject.userservice.utils.PasswordUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {

        user.setPassword(PasswordUtils.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole())
                .build()
        ).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    //TODO Validação de senha basica aqui. Melhorar
    public boolean login(String username, String password) throws CredentialNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && PasswordUtils.matches(password, user.get().getPassword())) {
            return true;
        }
        else {

            throw new CredentialNotFoundException("Invalid user or password.");
        }
    }
}
