package com.rafaelrosa.scheduleproject.schedulingservicecreation.feign;

import com.rafaelrosa.scheduleproject.commonentities.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable Long id);
}
