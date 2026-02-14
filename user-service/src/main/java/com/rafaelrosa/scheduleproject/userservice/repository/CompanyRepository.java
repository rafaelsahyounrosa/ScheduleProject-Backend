package com.rafaelrosa.scheduleproject.userservice.repository;

import com.rafaelrosa.scheduleproject.userservice.dto.CompanyView;
import com.rafaelrosa.scheduleproject.userservice.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Page<Company> findAll(Pageable pageable);

    @Query("select c from Company c where c.id = :id")
    Page<Company> findOneAsPage(@Param("id") Long id, Pageable pageable);

    Optional<Company> findByName(String name);

    @Query("""
        select c from Company c
        where
            lower(c.name) like lower(concat('%', :search, '%'))
    """)
    Page<Company> searchGlobal(@Param("search") String search, Pageable pageable);

    @Query("""
        select c from Company c
        where c.id = :companyId
          and (
                lower(c.name) like lower(concat('%', :search, '%'))
          )
    """)
    Page<Company> searchByCompany(
            @Param("companyId") Long companyId,
            @Param("search") String search,
            Pageable pageable
    );
}
