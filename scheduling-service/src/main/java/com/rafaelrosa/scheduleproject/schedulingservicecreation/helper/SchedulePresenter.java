package com.rafaelrosa.scheduleproject.schedulingservicecreation.helper;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.feign.CompanyClient;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.feign.CustomerClient;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.ScheduleView;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulePresenter {

    private final CustomerClient customers;
    private final CompanyClient companies;

    public ScheduleView toView(Scheduling s) {
        String customerName = null;
        try {
            var c = customers.getCustomerSummary(s.getCustomerId());
            customerName = c.name();
        } catch (FeignException e) {
            log.warn("[NAMES] customer {} failed: status={} {}", s.getCustomerId(), e.status(), e.getMessage());
        }

        String companyName = null;
        try {
            var comp = companies.getCompanySummary(s.getCompanyId());
            companyName = comp.name();
        } catch (FeignException e) {
            log.warn("[NAMES] company {} failed: status={} {}", s.getCompanyId(), e.status(), e.getMessage());
        }

        // status: aceita enum OU string
        String statusStr;
        var st = s.getStatus();
        if (st == null) {
            statusStr = null;
        } else if (st instanceof Enum<?> e) {
            statusStr = e.name();
        } else {
            statusStr = st.toString(); // já era String
        }

        // startTime: se sua entidade for LocalDateTime, converta
        java.time.LocalDate start;
        var startFromEntity = s.getStartTime();

        return new ScheduleView(
                s.getId(),
                startFromEntity,
                s.getDescription(),
                statusStr,
                s.getCustomerId(),
                s.getCompanyId(),
                customerName,
                companyName
        );
    }
}