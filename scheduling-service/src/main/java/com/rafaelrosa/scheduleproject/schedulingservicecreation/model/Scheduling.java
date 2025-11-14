package com.rafaelrosa.scheduleproject.schedulingservicecreation.model;

import com.rafaelrosa.scheduleproject.commonentities.SchedulingDTO;
import com.rafaelrosa.scheduleproject.commonentities.enums.SchedulingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedulings")
@AllArgsConstructor
@NoArgsConstructor
public class Scheduling extends SchedulingDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDate startTime;
    private String description;
    @Enumerated(EnumType.STRING)
    private SchedulingStatus status;
    private Long customerId;
    private Long companyId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public LocalDate getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public SchedulingStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(SchedulingStatus status) {
        this.status = status;
    }

    @Override
    public Long getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
