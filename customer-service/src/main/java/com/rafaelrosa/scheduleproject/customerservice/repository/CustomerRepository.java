package com.rafaelrosa.scheduleproject.customerservice.repository;

import com.rafaelrosa.scheduleproject.customerservice.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
