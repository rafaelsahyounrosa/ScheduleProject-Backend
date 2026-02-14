package com.rafaelrosa.scheduleproject.userservice.service;


import com.rafaelrosa.scheduleproject.commonentities.enums.Roles;
import com.rafaelrosa.scheduleproject.userservice.dto.CreateCompanyAdminRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UpdateCompanyAdminRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.model.Company;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.repository.CompanyRepository;
import com.rafaelrosa.scheduleproject.userservice.repository.UserRepository;
import com.rafaelrosa.scheduleproject.userservice.security.Authz;
import com.rafaelrosa.scheduleproject.userservice.utils.PasswordUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyAdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final Authz authz;

    private static final int MAX_PAGE_SIZE = 50;
    private Pageable clamp(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }


    public CompanyAdminService(UserRepository userRepository, CompanyRepository companyRepository, Authz authz) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.authz = authz;
    }

    public UserView create(CreateCompanyAdminRequest req){

        Company company = companyRepository.findById(req.companyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + req.companyId()));

        User u = new User();
        u.setUsername(req.username());
        u.setPassword(PasswordUtils.encode(req.password()));
        u.setName(req.name());
        u.setEmail(req.email());
        u.setRole(Roles.COMPANY_ADMIN.name());
        u.setCompany(company);

        return UserView.from(userRepository.save(u));
    }

    public UserView update(Long id, UpdateCompanyAdminRequest req){

        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if(u.getRole() != Roles.COMPANY_ADMIN.name()){
            throw new IllegalArgumentException("User with id: " + id + " is not a COMPANY_ADMIN");
        }

        if(req.name() != null) u.setName(req.name());
        if(req.email() != null) u.setEmail(req.email());
        return UserView.from(userRepository.save(u));
    }

    public void delete(Long id){

        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (u.getRole() != Roles.COMPANY_ADMIN.name()) {
            throw new IllegalArgumentException("User with id: " + id + " is not a COMPANY_ADMIN");
        }

        userRepository.delete(u);
    }

    @Transactional(readOnly = true)
    public Page<UserView> findAllByRole(String search, Pageable pageable){

        Pageable safe = clamp(pageable);

        String normalizedSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();
        Boolean hasSearch = normalizedSearch != null && normalizedSearch.length() > 2;

        if (authz.isAdmin()){

            if(hasSearch){

                return userRepository.searchGlobalByRole(normalizedSearch, safe, Roles.COMPANY_ADMIN).map(UserView::from);
            }

            return userRepository.findAllByRole(Roles.COMPANY_ADMIN, safe)
                    .map(UserView::from);
        }

        Long cid = authz.currentCompanyId();
        if(cid == null) throw new AccessDeniedException("Your token has no company scope");

        if(hasSearch){
            return userRepository.searchByCompanyAndRole(cid, Roles.COMPANY_ADMIN,normalizedSearch, safe)
                    .map(UserView::from);

        }

        return userRepository.findAllByRoleAndCompany_Id(Roles.COMPANY_ADMIN ,cid, safe)
                .map(UserView::from);
    }
}
