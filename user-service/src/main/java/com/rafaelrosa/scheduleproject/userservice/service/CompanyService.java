package com.rafaelrosa.scheduleproject.userservice.service;

import com.rafaelrosa.scheduleproject.commonentities.CompanySummaryDTO;
import com.rafaelrosa.scheduleproject.userservice.dto.*;
import com.rafaelrosa.scheduleproject.userservice.model.Company;
import com.rafaelrosa.scheduleproject.userservice.model.CompanyDetails;
import com.rafaelrosa.scheduleproject.userservice.repository.CompanyRepository;
import com.rafaelrosa.scheduleproject.userservice.security.Authz;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompanyService {

    private CompanyRepository companyRepository;
    private final Authz  authz;

    //
    private static final int MAX_PAGE_SIZE = 50;
    private Pageable clamp(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    public CompanyService(CompanyRepository companyRepository, Authz authz) {
        this.companyRepository = companyRepository;
        this.authz = authz;
    }

    @Transactional(readOnly = true)
    public Page<CompanyView> findAll(String search, Pageable pageable) {

        Pageable safe = clamp(pageable);

        String normalizedSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();
        Boolean hasSearch = normalizedSearch != null && normalizedSearch.length() > 2;

            if (authz.isAdmin()) {

                if(hasSearch) {
                    return companyRepository.searchGlobal(normalizedSearch, safe).map(CompanyView::from);
                }

                return companyRepository.findAll(safe).map(c -> CompanyView.from(c));
            }
            Long cid = authz.currentCompanyId();
            if (cid == null) {
                throw new AccessDeniedException("Your token has no company scope");
            } else {

                if(hasSearch) {
                    return companyRepository.searchByCompany(cid, normalizedSearch, safe).map(CompanyView::from);
                }

                return companyRepository.findOneAsPage(cid, safe).map(c -> CompanyView.from(c));
            }
    }


    @Transactional
    public CompanyView saveCompany(CreateCompanyRequest req) {
        final boolean admin = authz.isAdmin();
        Long targetCompanyId;

        if (!admin) {
           throw new AccessDeniedException("You are not allowed to create a company.");
        }
        if(companyRepository.findByName(req.name()).isPresent()) {
            throw new AccessDeniedException("Company already exists.");
        }

        Company company = new Company();
        company.setName(req.name());
        company.setDetails(toDetails(req.details()));
        return CompanyView.from(companyRepository.save(company));
    }

    @Transactional
    public CompanyView updateCompany(Long idUrl , UpdateCompanyRequest req) {

        Company c = companyRepository.findById(idUrl).orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        final boolean admin = authz.isAdmin();
        Long targetCompanyId;

        try {
            if(!admin){

                targetCompanyId = authz.currentCompanyId();
                if(targetCompanyId == null || !c.getId().equals(targetCompanyId)){
                    throw new AccessDeniedException("Access denied for this company");
                }
            }

            if (req.name() != null) c.setName(req.name());
            if (req.details() != null) {
                CompanyDetails current = c.getDetails();
                CompanyDetails incoming = toDetails(req.details());

                if (current == null) {
                    c.setDetails(incoming);
                } else {
                    // merge “campo a campo”
                    if (req.details().standardMessage() != null)
                        current.setStandardMessage(incoming.getStandardMessage());

                    if (req.details().returnMessageTimeInDays() != null)
                        current.setReturnMessageTimeInDays(incoming.getReturnMessageTimeInDays());

                    if (req.details().extras() != null && !req.details().extras().isEmpty()) {
                        // merge dos extras (sobrescreve chaves existentes)
                        current.getExtras().putAll(incoming.getExtras());
                    }
                }
            }
            return CompanyView.from(companyRepository.save(c));


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteCompany(Long idUrl) {
        final boolean admin = authz.isAdmin();

        if(!admin){
            throw new AccessDeniedException("You are not allowed to delete this company.");
        }
        if(!companyRepository.findById(idUrl).isPresent()) {
            throw new EntityNotFoundException("Company Not Found");
        }

        companyRepository.deleteById(idUrl);
    }

    private CompanyDetails toDetails(CompanyDetailsPayload p) {
        if (p == null) return null;
        CompanyDetails d = new CompanyDetails();
        d.setStandardMessage(p.standardMessage());
        if (p.returnMessageTimeInDays() != null) {
            d.setReturnMessageTimeInDays(p.returnMessageTimeInDays());
        }
        d.getExtras().putAll(p.extras() == null ? Map.of() : p.extras());
        return d;
    }

    @Transactional(readOnly = true)
    public Optional<CompanySummary> findSummaryScoped(Long id) {
        if (authz.isAdmin()) {
            return companyRepository.findById(id).map(CompanySummary::from);
        }
        Long cid = authz.currentCompanyId();
        if (cid == null) throw new AccessDeniedException("Your token has no company scope");
        if (!cid.equals(id)) throw new AccessDeniedException("Forbidden company");
        return companyRepository.findById(id).map(CompanySummary::from);
    }

    @Transactional(readOnly = true)
    public List<CompanySummaryDTO> getSummariesBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        if (authz.isAdmin()) {
            // findAllById retorna Iterable -> converte com StreamSupport
            return java.util.stream.StreamSupport.stream(companyRepository.findAllById(ids).spliterator(), false)
                    .map(c -> new CompanySummaryDTO(c.getId(), c.getName()))
                    .toList();
        }

        Long cid = authz.currentCompanyId();
        if (cid == null) throw new AccessDeniedException("Your token has no company scope");

        // não-admin só pode obter a própria company
        if (!ids.contains(cid)) return List.of();

        return companyRepository.findById(cid)
                .map(c -> List.of(new CompanySummaryDTO(c.getId(), c.getName())))
                .orElse(List.of());
    }

}
