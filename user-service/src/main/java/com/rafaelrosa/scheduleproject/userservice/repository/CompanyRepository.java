package com.rafaelrosa.scheduleproject.userservice.repository;

import com.rafaelrosa.scheduleproject.userservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
