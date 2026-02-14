package com.rafaelrosa.scheduleproject.customerservice.controller;

import com.rafaelrosa.scheduleproject.commonentities.CustomerSummaryDTO;
import com.rafaelrosa.scheduleproject.customerservice.model.Customer;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.CreateCustomerRequest;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.CustomerSummary;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.CustomerView;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.UpdateCustomerRequest;
import com.rafaelrosa.scheduleproject.customerservice.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerView> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerView>> getAllCustomers(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getAllCustomers(search, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<CustomerView> saveCustomer(@RequestBody CreateCustomerRequest customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.saveCustomer(customer));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerView> updateCustomer(@PathVariable("id") Long idURL, @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(idURL, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<CustomerSummary> summary(@PathVariable Long id) {
        return ResponseEntity.of(customerService.findSummaryScoped(id));
    }

    //Feign Scheduling-service
    @GetMapping("/summary/batch")
    public List<CustomerSummaryDTO> getCustomersSummaryBatch(@RequestParam List<Long> ids) {
        return customerService.getSummaryBatch(ids);
    }

}
