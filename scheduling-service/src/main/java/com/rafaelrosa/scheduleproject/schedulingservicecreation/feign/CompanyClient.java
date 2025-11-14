package com.rafaelrosa.scheduleproject.schedulingservicecreation.feign;

import com.rafaelrosa.scheduleproject.commonentities.CompanySummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface CompanyClient {

    @GetMapping("/companies/{id}/summary")
    CompanySummary getCompanySummary(@PathVariable Long id);
}
