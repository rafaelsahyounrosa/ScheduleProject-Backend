package com.rafaelrosa.scheduleproject.userservice.repository;

import com.rafaelrosa.scheduleproject.commonentities.enums.Roles;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Page<User> findAllByRole(Roles role, Pageable pageable);
    Page<User> findAllByRoleAndCompany_Id(Roles role, Long companyId, Pageable pageable);
}
