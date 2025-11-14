package com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateScheduleRequest(
        @NotBlank @Past LocalDate startTime,
        @NotBlank String description,
        @NotBlank String status
) {
}
