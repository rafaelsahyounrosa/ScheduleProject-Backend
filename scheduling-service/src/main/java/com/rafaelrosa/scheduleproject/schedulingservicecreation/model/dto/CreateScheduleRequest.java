package com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CreateScheduleRequest(
        @NotBlank @Past LocalDate startTime,
        @NotBlank String description,
        @NotBlank String status,
        @NotBlank Long customerId,
        @NotBlank Long companyId
        ) {}

