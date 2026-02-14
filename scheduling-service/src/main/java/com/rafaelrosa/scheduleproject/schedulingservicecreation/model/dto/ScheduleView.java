package com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduleView(
        Long id,
        LocalDateTime startTime,
        String description,
        String status,
        Long customerId,
        Long companyId,
        String customerName,
        String companyName
) {
    public static ScheduleView from(Scheduling s, String customerName, String companyName) {
        return new ScheduleView(
                s.getId(),
                s.getStartTime(),
                s.getDescription(),
                s.getStatus().name(),
                s.getCustomerId(),
                s.getCompanyId(),
                customerName,
                companyName
        );
    }

    public static ScheduleView from(Scheduling s) {
        return from(s, null, null);
    }
}
