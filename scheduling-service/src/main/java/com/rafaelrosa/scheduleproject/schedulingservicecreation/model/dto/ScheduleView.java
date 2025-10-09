package com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record ScheduleView(
        Long id,
        LocalDate startTime,
        String description,
        String status,
        Long customerId,
        Long companyId
) {
    public static ScheduleView from(Scheduling s){
        return new ScheduleView(
                s.getId(),
                s.getStartTime(),
                s.getDescription(),
                s.getStatus().name(),
                s.getCustomerId(),
                s.getCompanyId()
        );
    }
}
