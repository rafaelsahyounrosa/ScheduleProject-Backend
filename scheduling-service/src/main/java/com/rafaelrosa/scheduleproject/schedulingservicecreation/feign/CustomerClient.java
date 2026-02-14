package com.rafaelrosa.scheduleproject.schedulingservicecreation.feign;

import com.rafaelrosa.commonsecurity.CommonSecurityAutoConfiguration;
import com.rafaelrosa.scheduleproject.commonentities.CustomerDTO;
import com.rafaelrosa.scheduleproject.commonentities.CustomerSummary;
import com.rafaelrosa.scheduleproject.commonentities.CustomerSummaryDTO;
import com.rafaelrosa.scheduleproject.commonentities.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "customer-service",
        configuration = CommonSecurityAutoConfiguration.class)
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable Long id);

    @GetMapping("/api/customers/{id}/summary")
    CustomerSummary getCustomerSummary(@PathVariable Long id);

    @GetMapping("/api/customers")
    PageResponse<CustomerDTO> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String sort
    );

    @GetMapping("/api/customers/summary/batch")
    List<CustomerSummaryDTO> getCustomerSummaries(@RequestParam List<Long> ids);

}
