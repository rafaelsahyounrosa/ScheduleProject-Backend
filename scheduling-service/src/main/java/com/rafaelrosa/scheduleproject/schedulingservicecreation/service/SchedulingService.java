package com.rafaelrosa.scheduleproject.schedulingservicecreation.service;

import com.rafaelrosa.commonsecurity.Authz;
import com.rafaelrosa.scheduleproject.commonentities.CustomerDTO;
import com.rafaelrosa.scheduleproject.commonentities.PageResponse;
import com.rafaelrosa.scheduleproject.commonentities.enums.SchedulingStatus;
import com.rafaelrosa.scheduleproject.commonentities.exceptions.NotFoundException;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.feign.CompanyClient;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.feign.CustomerClient;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.helper.SchedulePresenter;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.CreateScheduleRequest;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.ScheduleView;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.dto.UpdateScheduleRequest;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.repository.SchedulingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SchedulingService {

    private final SchedulingRepository schedulingRepository;
    private final CustomerClient customers;
    private final CompanyClient companies;
    private final Authz authz;
    private final SchedulePresenter presenter;

    //TODO evoluir para batch ao invés de cache local por performance.
    /*
    * Depois (médio prazo): crie endpoints batch:
        POST /api/customers/summaries:batch com body [ids]
        POST /api/companies/summaries:batch com body [ids]
        e resolva nomes em 2 chamadas por página (sem N+1), usando collect(distinct ids).*/
    // pode trocar por Caffeine/Cacheable depois
    private final Map<Long, String> customerNameCache = new ConcurrentHashMap<>();
    private final Map<Long, String> companyNameCache  = new ConcurrentHashMap<>();

    private static final int MAX_PAGE_SIZE = 50;
    private Pageable clamp(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    public SchedulingService(SchedulingRepository schedulingRepository, CustomerClient customerClient, CompanyClient companies, Authz authz, SchedulePresenter presenter) {
        this.schedulingRepository = schedulingRepository;
        this.customers = customerClient;
        this.companies = companies;
        this.authz = authz;
        this.presenter = presenter;
    }

    @Transactional
    public ScheduleView scheduleCustomer(CreateScheduleRequest req){

        final boolean admin = authz.isAdmin();

        //TODO por enquanto está sem uso, mas pode ser que seja legal ter o user completo caso queira mandar algum email ou mensagem de confirmação do agendamento
        var customer = customers.getCustomerById(req.customerId());
        Long customerCompanyId = customer.getCompanyId();

        Long targetCompanyId = admin ? customerCompanyId : authz.currentCompanyId();

        if(targetCompanyId == null){
            throw new AccessDeniedException("Your token has no company scope");
        }
        if(!admin && !targetCompanyId.equals(customerCompanyId)){
            throw new AccessDeniedException("Your token has no company scope for this user");
        }

        Scheduling s = new Scheduling();
        s.setCustomerId(req.customerId());
        s.setCompanyId(targetCompanyId);
        s.setStartTime(req.startTime());
        s.setDescription(req.description());
        s.setStatus(SchedulingStatus.valueOf(req.status()));

        return ScheduleView.from(schedulingRepository.save(s));
    }

    @Transactional(readOnly = true)
    public Page<ScheduleView> findAll(String search, Pageable pageable) {

        Pageable safe = clamp(pageable);

        String normalizedSearch = (search == null || search.trim().isEmpty())
                ? null
                : search.trim();

        boolean hasSearch = normalizedSearch != null && normalizedSearch.length() > 2;

        Page<Scheduling> page;

        // Sem search
        if (!hasSearch) {
            if (authz.isAdmin()) {
                page = schedulingRepository.findAll(safe);
            } else {
                Long cid = requireCompanyId();
                page = schedulingRepository.findAllByCompanyId(cid, safe);
            }
            return presenter.toViewPage(page);
        }

        // Com search: resolve customerIds via Feign (limitado)
        List<Long> customerIds = resolveCustomerIds(normalizedSearch);

        // Aplica query correta (admin vs escopado)
        if (authz.isAdmin()) {
            if (customerIds.isEmpty()) {
                page = schedulingRepository.searchGlobalLocal(normalizedSearch, safe);
            } else {
                page = schedulingRepository.searchGlobal(normalizedSearch, customerIds, safe);
            }
            return presenter.toViewPage(page);
        }

        Long cid = requireCompanyId();

        if (customerIds.isEmpty()) {
            page = schedulingRepository.searchByCompanyLocal(cid, normalizedSearch, safe);
        } else {
            page = schedulingRepository.searchByCompany(cid, normalizedSearch, customerIds, safe);
        }

        return presenter.toViewPage(page);
    }


    @Transactional(readOnly = true)
    public ScheduleView findById(Long id) {

        if(authz.isAdmin()){
            return schedulingRepository.findById(id).map(ScheduleView::from).orElse(null);
        }

        Long cid = authz.currentCompanyId();
        if(cid == null) throw new NotFoundException("Your token has no company scope");
        //TODO tratar null no response entity com empty ao inves do HTTP 200 ok?
        return schedulingRepository.findByIdAndCompanyId(id, cid).map(ScheduleView::from).orElse(null);
    }

    public void deleteById(Long id) {

        Scheduling s = schedulingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scheduling with id " + id + " not found"));

        if(!authz.isAdmin()){
            Long cid = authz.currentCompanyId();
            if(cid == null || !cid.equals(s.getCompanyId())){
                throw new AccessDeniedException("Your token has no company scope for this user");
            }
        }

        schedulingRepository.deleteById(id);
    }

    @Transactional
    public ScheduleView updateSchedule(Long idUrl, UpdateScheduleRequest req) {

        Scheduling s = schedulingRepository.findById(idUrl)
                .orElseThrow(() -> new EntityNotFoundException("Scheduling with id " + idUrl + " not found"));

        if(!authz.isAdmin()){
            Long cid = authz.currentCompanyId();
            if(cid == null || !cid.equals(s.getCompanyId())){
                throw new AccessDeniedException("Your token has no company scope for this user");
            }
        }

        if(req.description() != null) s.setDescription(req.description());
        if(req.startTime() != null) s.setStartTime(req.startTime());
        if(req.status() != null) s.setStatus(SchedulingStatus.valueOf(req.status()));
        return ScheduleView.from(schedulingRepository.save(s));
    }

    private Long requireCompany() {
        Long cid = authz.currentCompanyId();
        if (cid == null) throw new AccessDeniedException("Your token has no company scope");
        return cid;
    }

    private String safeCustomerName(Long id) {
        try {
            return customers.getCustomerSummary(id).name();
        } catch (Exception e) {
            return "Customer #" + id; // fallback
        }
    }
    private String safeCompanyName(Long id) {
        try {
            return companies.getCompanySummary(id).name();
        } catch (Exception e) {
            return "Company #" + id; // fallback
        }
    }

    // helper
    private Long requireCompanyId() {
        Long cid = authz.currentCompanyId();
        if (cid == null) throw new AccessDeniedException("Your token has no company scope");
        return cid;
    }

    private List<Long> resolveCustomerIds(String search) {
        try {
            PageResponse<CustomerDTO> page = customers.findAll(search, 0, 100, "lastName,asc");
            if (page.getContent() == null) return List.of();
            return page.getContent().stream()
                    .map(CustomerDTO::getId)
                    .distinct()
                    .toList();
        } catch (Exception e) {
            return List.of(); // fallback: search local
        }
    }

}
