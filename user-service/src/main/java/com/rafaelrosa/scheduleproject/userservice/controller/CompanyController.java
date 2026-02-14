package com.rafaelrosa.scheduleproject.userservice.controller;

import com.rafaelrosa.scheduleproject.commonentities.CompanySummaryDTO;
import com.rafaelrosa.scheduleproject.userservice.dto.CompanySummary;
import com.rafaelrosa.scheduleproject.userservice.dto.CompanyView;
import com.rafaelrosa.scheduleproject.userservice.dto.CreateCompanyRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UpdateCompanyRequest;
import com.rafaelrosa.scheduleproject.userservice.service.CompanyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyView> create(@RequestBody CreateCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.saveCompany(request));
    }


    @PreAuthorize("hasRole('COMPANY_ADMIN') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<CompanyView>> getCompanies(@RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(companyService.findAll(search, pageable));
    }

    @PreAuthorize("hasRole('COMPANY_ADMIN') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyView> update(@PathVariable Long id, @RequestBody UpdateCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(companyService.updateCompany(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<CompanySummary> summary(@PathVariable Long id, HttpServletRequest request) {
        //TODO remover log
        System.out.println("[COMPANY-SUMMARY] auth= " + request.getHeader("Authorization"));
        return ResponseEntity.of(companyService.findSummaryScoped(id));
    }

    @PreAuthorize("hasRole('COMPANY_ADMIN') or hasRole('ADMIN')")
    @GetMapping("/summary/batch")
    public List<CompanySummaryDTO> getCompanySummaries(@RequestParam List<Long> ids) {
        return companyService.getSummariesBatch(ids);
    }
}
