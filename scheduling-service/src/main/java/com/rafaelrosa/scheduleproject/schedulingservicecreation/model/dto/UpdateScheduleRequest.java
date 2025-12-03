package com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateScheduleRequest(
        @NotNull @FutureOrPresent LocalDateTime startTime,
        @NotBlank String description,
        @NotBlank String status
) {
}
