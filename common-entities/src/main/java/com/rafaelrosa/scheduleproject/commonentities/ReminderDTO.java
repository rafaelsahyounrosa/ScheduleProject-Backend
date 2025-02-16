package com.rafaelrosa.scheduleproject.commonentities;

import com.rafaelrosa.scheduleproject.commonentities.enums.ReminderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReminderDTO {

    private Long id;
    private CustomerDTO customer;
    private SchedulingDTO scheduling;
    private LocalDate dateToSendReminder;
    private String reminderText;
    private ReminderStatus reminderStatus;

}
