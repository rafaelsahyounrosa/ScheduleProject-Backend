package com.rafaelrosa.scheduleproject.userservice.dto;

public record UpdateCompanyRequest(
        String name,                       // null = não alterar
        CompanyDetailsPayload details      // null = não alterar
) {}
