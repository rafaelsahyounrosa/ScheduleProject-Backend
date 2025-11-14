package com.rafaelrosa.scheduleproject.customerservice.model.dto;

import com.rafaelrosa.scheduleproject.customerservice.model.Customer;

public record CustomerSummary(Long id, String name) {
    public static CustomerSummary from(Customer c) {
        return new CustomerSummary(c.getId(), c.getFirstName() + " " + c.getLastName());
    }
}
