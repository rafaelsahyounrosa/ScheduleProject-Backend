package com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateScheduleRequest(
        @NotNull @FutureOrPresent LocalDateTime startTime,
        @NotBlank String description,
        @NotBlank String status,
        @NotNull Long customerId
        //@NotBlank Long companyId
        ) {}

