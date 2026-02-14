package com.rafaelrosa.scheduleproject.userservice.controller;


import com.rafaelrosa.scheduleproject.userservice.dto.CreateCompanyAdminRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UpdateCompanyAdminRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.service.CompanyAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company-admins")
public class AdminCompanyAdminController {

    private final CompanyAdminService companyAdminService;

    public AdminCompanyAdminController(CompanyAdminService companyAdminService) {
        this.companyAdminService = companyAdminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserView> createCompanyAdmin(@RequestBody CreateCompanyAdminRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyAdminService.create(req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public ResponseEntity<UserView> updateCompanyAdmin(@PathVariable Long id, @RequestBody UpdateCompanyAdminRequest req) {
        return ResponseEntity.ok(companyAdminService.update(id, req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserView> deleteCompanyAdmin(@PathVariable Long id) {
        companyAdminService.delete(id);
        return  ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserView>> getCompanyAdmins(@RequestParam(required = false) String search,
            Pageable pageable) {
         return ResponseEntity.ok(companyAdminService.findAllByRole(search, pageable));
    }

    //TODO criar um endpoint de promote e demote de usuários. Verificar se pode ser aberto para company Admins também

}
