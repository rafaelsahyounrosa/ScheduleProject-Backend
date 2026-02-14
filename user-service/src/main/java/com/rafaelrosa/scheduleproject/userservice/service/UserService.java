package com.rafaelrosa.scheduleproject.userservice.service;

import com.rafaelrosa.scheduleproject.commonentities.enums.Roles;
import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.repository.UserRepository;
import com.rafaelrosa.scheduleproject.userservice.security.Authz;
import com.rafaelrosa.scheduleproject.userservice.utils.PasswordUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialNotFoundException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Authz authz;

    private static final int MAX_PAGE_SIZE = 50;
    private Pageable clamp(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    public UserService(UserRepository userRepository, Authz authz) {
        this.userRepository = userRepository;
        this.authz = authz;
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
    @Transactional(readOnly = true)
    public Page<UserView> findAll(String search , Pageable pageable) {

        Pageable safe = clamp(pageable);

        String normalizedSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();
        Boolean hasSearch = normalizedSearch != null && normalizedSearch.length() > 2;

        //if (authz.isAdmin()){

            if(hasSearch){

                return userRepository.searchGlobalByRole(normalizedSearch, safe, Roles.ADMIN).map(UserView::from);
            }

            return userRepository.findAllByRole(Roles.ADMIN, safe)
                    .map(UserView::from);
        //}
        //else throw new AccessDeniedException("Your token has no company scope");

        //var users = userRepository.findAll(pageable).map(u -> UserView.from(u));
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found")));
    }
}
