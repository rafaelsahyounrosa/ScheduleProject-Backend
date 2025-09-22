package com.rafaelrosa.scheduleproject.userservice.controller;

import com.rafaelrosa.scheduleproject.userservice.dto.CreateCollaboratorRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UpdateCollaboratorRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.service.CollaboratorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collaborators")
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    public CollaboratorController(CollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    @PreAuthorize("hasRole('COMPANY_ADMIN') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserView> create(@RequestBody CreateCollaboratorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaboratorService.create(request));
    }

    @PreAuthorize("hasRole('COMPANY_ADMIN') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserView> update(@PathVariable Long id, @RequestBody UpdateCollaboratorRequest request) {
        return ResponseEntity.ok(collaboratorService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        collaboratorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    @GetMapping
    public Page<UserView> list(Pageable pageable){
        return collaboratorService.list(pageable);
    }
}
