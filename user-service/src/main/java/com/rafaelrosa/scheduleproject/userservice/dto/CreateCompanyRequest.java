package com.rafaelrosa.scheduleproject.userservice.dto;


public record CreateCompanyRequest(
        String name,
        CompanyDetailsPayload details
) {}
