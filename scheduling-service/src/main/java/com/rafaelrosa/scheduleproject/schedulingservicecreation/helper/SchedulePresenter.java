package com.rafaelrosa.scheduleproject.schedulingservicecreation.helper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rafaelrosa.scheduleproject.commonentities.CompanySummaryDTO;
import com.rafaelrosa.scheduleproject.commonentities.CustomerSummaryDTO;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.feign.CompanyClient;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.feign.CustomerClient;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.ScheduleView;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulePresenter {

    private final CustomerClient customers;
    private final CompanyClient companies;

    // Cache com TTL e limite (evita crescer infinito)
    private final Cache<Long, String> customerNameCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(15))
            .build();

    private final Cache<Long, String> companyNameCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(Duration.ofMinutes(60))
            .build();

    public ScheduleView toView(Scheduling s) {
        // Usa o mesmo pipeline de cache/batch (batch de 1 item)
        Set<Long> customerIds = (s.getCustomerId() == null)
                ? Collections.emptySet()
                : Collections.singleton(s.getCustomerId());

        Set<Long> companyIds = (s.getCompanyId() == null)
                ? Collections.emptySet()
                : Collections.singleton(s.getCompanyId());

        Map<Long, String> customerNames = resolveCustomerNames(customerIds);
        Map<Long, String> companyNames  = resolveCompanyNames(companyIds);

        return ScheduleView.from(
                s,
                customerNames.get(s.getCustomerId()),
                companyNames.get(s.getCompanyId())
        );
    }

    public Page<ScheduleView> toViewPage(Page<Scheduling> page) {
        List<Scheduling> items = page.getContent();

        Set<Long> customerIds = items.stream()
                .map(Scheduling::getCustomerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> companyIds = items.stream()
                .map(Scheduling::getCompanyId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> customerNames = resolveCustomerNames(customerIds);
        Map<Long, String> companyNames  = resolveCompanyNames(companyIds);

        return page.map(s -> ScheduleView.from(
                s,
                customerNames.get(s.getCustomerId()),
                companyNames.get(s.getCompanyId())
        ));
    }

    private Map<Long, String> resolveCustomerNames(Set<Long> ids) {
        Map<Long, String> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) return result;

        List<Long> missing = ids.stream()
                .filter(id -> customerNameCache.getIfPresent(id) == null)
                .toList();

        if (!missing.isEmpty()) {
            try {
                // batch endpoint
                List<CustomerSummaryDTO> summaries = customers.getCustomerSummaries(missing);
                for (CustomerSummaryDTO s : summaries) {
                    customerNameCache.put(s.id(), s.name());
                }
            } catch (FeignException e) {
                log.warn("[BATCH NAMES] customer batch failed: status={} {}", e.status(), e.getMessage());

                // fallback individual limitado (evita N+1 gigante)
                int limit = Math.min(missing.size(), 10);
                for (int i = 0; i < limit; i++) {
                    Long id = missing.get(i);
                    try {
                        var single = customers.getCustomerSummary(id);
                        customerNameCache.put(id, single.name());
                    } catch (FeignException ex) {
                        log.warn("[NAMES] customer {} failed: status={} {}", id, ex.status(), ex.getMessage());
                    }
                }
            }
        }

        for (Long id : ids) {
            result.put(id, customerNameCache.getIfPresent(id));
        }
        return result;
    }

    private Map<Long, String> resolveCompanyNames(Set<Long> ids) {
        Map<Long, String> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) return result;

        List<Long> missing = ids.stream()
                .filter(id -> companyNameCache.getIfPresent(id) == null)
                .toList();

        if (!missing.isEmpty()) {
            try {
                List<CompanySummaryDTO> summaries = companies.getCompanySummaries(missing);
                for (CompanySummaryDTO s : summaries) {
                    companyNameCache.put(s.id(), s.name());
                }
            } catch (FeignException e) {
                log.warn("[BATCH NAMES] company batch failed: status={} {}", e.status(), e.getMessage());

                int limit = Math.min(missing.size(), 10);
                for (int i = 0; i < limit; i++) {
                    Long id = missing.get(i);
                    try {
                        var single = companies.getCompanySummary(id);
                        companyNameCache.put(id, single.name());
                    } catch (FeignException ex) {
                        log.warn("[NAMES] company {} failed: status={} {}", id, ex.status(), ex.getMessage());
                    }
                }
            }
        }

        for (Long id : ids) {
            result.put(id, companyNameCache.getIfPresent(id));
        }
        return result;
    }
}
