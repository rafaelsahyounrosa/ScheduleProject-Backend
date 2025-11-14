package com.rafaelrosa.scheduleproject.userservice.dto;

import com.rafaelrosa.scheduleproject.userservice.model.Company;

public record CompanySummary(Long id, String name) {
    public static CompanySummary from(Company c) {
        return new CompanySummary(c.getId(), c.getName());
    }
}
