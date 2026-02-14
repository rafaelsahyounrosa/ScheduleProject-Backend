package com.rafaelrosa.scheduleproject.schedulingservicecreation.feign;

import com.rafaelrosa.scheduleproject.commonentities.CompanySummary;
import com.rafaelrosa.scheduleproject.commonentities.CompanySummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface CompanyClient {

    @GetMapping("/companies/{id}/summary")
    CompanySummary getCompanySummary(@PathVariable Long id);

    @GetMapping("/companies/summary/batch")
    List<CompanySummaryDTO> getCompanySummaries(@RequestParam List<Long> ids);

}
