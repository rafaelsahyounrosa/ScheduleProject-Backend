package com.rafaelrosa.scheduleproject.schedulingservicecreation.controller;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.CreateScheduleRequest;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.ScheduleView;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.UpdateScheduleRequest;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.service.SchedulingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/schedulings")
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/create")
    public ResponseEntity<ScheduleView> createScheduling(@RequestBody CreateScheduleRequest schedule) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schedulingService.scheduleCustomer(schedule));
    }

    @GetMapping
    public ResponseEntity<Page<ScheduleView>> findAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(schedulingService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleView> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(schedulingService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        schedulingService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ScheduleView> updateSchedule(@PathVariable("id") Long idURL, @RequestBody UpdateScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(schedulingService.updateSchedule(idURL, request));
    }
}
