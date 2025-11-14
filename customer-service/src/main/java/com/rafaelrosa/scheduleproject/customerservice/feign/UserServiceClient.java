package com.rafaelrosa.scheduleproject.customerservice.feign;

import com.rafaelrosa.scheduleproject.commonentities.CompanyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    //TODO implementar endpoint de trazer o companyId buscando pelo ID do user

    @GetMapping("/api/users/companies/{id}")
    CompanyDTO getCompanyById(@PathVariable Long id);
}
