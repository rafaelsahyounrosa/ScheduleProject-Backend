package com.rafaelrosa.scheduleproject.customerservice.model.dto;

import com.rafaelrosa.scheduleproject.customerservice.model.Customer;

import java.time.LocalDate;
import java.util.Date;

public record CustomerView(Long id,
                           String firstName,
                           String lastName,
                           String email,
                           String phone,
                           String address,
                           LocalDate birthDate,
                           Long companyId) {

    public static CustomerView from(Customer customer) {
        return new CustomerView(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getDateOfBirth(),
                customer.getCompanyId()
        );
    }
}
