package com.rafaelrosa.scheduleproject.userservice.dto;

public record CreateCompanyAdminRequest(String username, String password, String name, String email, Long companyId){}
