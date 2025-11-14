package com.rafaelrosa.scheduleproject.userservice.dto;

import com.rafaelrosa.scheduleproject.userservice.model.Company;

public record CompanyView(Long id, String name, CompanyDetailsView details) {

    public static CompanyView from(Company company) {
        return new CompanyView(
                company.getId(),
                company.getName(),
                CompanyDetailsView.from(company.getDetails())
        );
    }
}
