package com.rafaelrosa.scheduleproject.userservice.dto;

import java.util.Map;

public record CompanyDetailsPayload(
        String standardMessage,
        Integer returnMessageTimeInDays,          // Integer p/ permitir null no UPDATE
        Map<String, Object> extras                // pode ser null; tratar como Map.of()
) {}