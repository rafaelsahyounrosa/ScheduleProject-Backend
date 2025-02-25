package com.rafaelrosa.scheduleproject.schedulingservicecreation.controller;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.service.SchedulingService;
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
    public ResponseEntity<Scheduling> createScheduling(@RequestBody Scheduling scheduling) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schedulingService.scheduleCustomer(scheduling));
    }

    @GetMapping
    public ResponseEntity<Iterable<Scheduling>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(schedulingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Scheduling>> findById(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(schedulingService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        schedulingService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
