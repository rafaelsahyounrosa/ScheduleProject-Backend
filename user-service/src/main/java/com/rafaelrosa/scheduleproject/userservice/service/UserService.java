package com.rafaelrosa.scheduleproject.userservice.service;

import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.repository.UserRepository;
import com.rafaelrosa.scheduleproject.userservice.utils.PasswordUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //TODO Melhorar Logica: Somente admin pode adicionar outros admins
    //TODO Criar role Merchant Admin. Somente merchant Admin pode criar users dentro da empresa
    public User registerUser(User user) {

        user.setPassword(PasswordUtils.encode(user.getPassword()));
        return userRepository.save(user);
    }

    //TODO Validação de senha basica aqui. Melhorar
    public boolean login(String username, String password) throws CredentialNotFoundException {
        return userRepository.findByUsername(username)
                .filter(u -> PasswordUtils.matches(password, u.getPassword()))
                .map(u -> true)
                .orElseThrow(() -> new CredentialNotFoundException("Invalid username or password"));
    }

    //TODO implementar visibilidade limitada para não ADMINS
    public Page<UserView> findAll(Pageable pageable) {

        var users = userRepository.findAll(pageable).map(u -> UserView.from(u));

        return users;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found")));
    }
}
