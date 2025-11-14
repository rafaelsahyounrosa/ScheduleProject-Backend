package com.rafaelrosa.scheduleproject.customerservice.repository;

import com.rafaelrosa.scheduleproject.customerservice.model.Customer;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.CustomerView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Page<Customer> findAllByCompanyId(Long companyId, Pageable pageable);

    @Query(value = "select * from customers where id = :id and company_id = :companyId",
            nativeQuery = true)
    Optional<Customer> findByIdAndCompanyId(@Param("id") Long id,@Param("companyId") Long companyId);

    Page<Customer> findAll(Pageable pageable);
}
