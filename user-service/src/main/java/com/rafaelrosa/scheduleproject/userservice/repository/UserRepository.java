package com.rafaelrosa.scheduleproject.userservice.repository;

import com.rafaelrosa.scheduleproject.commonentities.enums.Roles;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Page<User> findAllByRole(Roles role, Pageable pageable);
    Page<User> findAllByRoleAndCompany_Id(Roles role, Long companyId, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    @Query("""
    select u from User u
    where u.role = :role
      and (
            lower(u.name) like lower(concat('%', :search, '%'))
         or lower(u.username) like lower(concat('%', :search, '%'))
         or lower(u.email) like lower(concat('%', :search, '%'))
      )
""")
    Page<User> searchGlobalByRole(@Param("search") String search, Pageable pageable, @Param("role") Roles role);

    @Query("""
    select u from User u
     where
         lower(u.name) like lower(concat('%', :search, '%'))
         or lower(u.username) like lower(concat('%', :search, '%'))
         or lower(u.email) like lower(concat('%', :search, '%'))
         or lower(u.role) like lower(concat('%', :search, '%'))
      
""")
    Page<User> searchGlobal(@Param("search") String search, Pageable pageable);

    @Query("""
    select u from User u
    where u.role = :role
      and u.company.id = :companyId
      and (
            lower(u.name) like lower(concat('%', :search, '%'))
         or lower(u.username) like lower(concat('%', :search, '%'))
         or lower(u.email) like lower(concat('%', :search, '%'))
      )
""")
    Page<User> searchByCompanyAndRole(@Param("companyId") Long companyId,
                                      @Param("role") Roles companyAdmin,
                                      @Param("search") String normalizedSearch, Pageable safe);
}
