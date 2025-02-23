package com.rafaelrosa.scheduleproject.schedulingservicecreation.controller;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.service.SchedulingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedulings")
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/create")
    public ResponseEntity<Scheduling> createScheduling(@RequestBody Scheduling scheduling) {
        //TODO Implementar exception handler
        return ResponseEntity.status(HttpStatus.CREATED).body(schedulingService.scheduleCustomer(scheduling));
    }

    @GetMapping
    public ResponseEntity<Iterable<Scheduling>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(schedulingService.findAll());
    }
}
