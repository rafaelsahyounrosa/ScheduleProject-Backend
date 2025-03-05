package com.rafaelrosa.scheduleproject.commonentities;

import com.rafaelrosa.scheduleproject.commonentities.enums.SchedulingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingDTO {

    private Long id;
    private LocalDateTime startTime;
    private String description;
    private SchedulingStatus status; //ENUM de Possíveis Status (CANCELED, CONFIRMED, SENT, etc)
    private Long customerId;
    private Long companyId;
}
