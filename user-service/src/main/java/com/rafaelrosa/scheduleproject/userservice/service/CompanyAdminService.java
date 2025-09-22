package com.rafaelrosa.scheduleproject.userservice.service;


import com.rafaelrosa.scheduleproject.commonentities.enums.Roles;
import com.rafaelrosa.scheduleproject.userservice.dto.CreateCompanyAdminRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UpdateCompanyAdminRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.model.Company;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.repository.CompanyRepository;
import com.rafaelrosa.scheduleproject.userservice.repository.UserRepository;
import com.rafaelrosa.scheduleproject.userservice.utils.PasswordUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CompanyAdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;


    public CompanyAdminService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
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

    public Page<UserView> findAllByRole(Pageable pageable){
        return userRepository.findAllByRole(Roles.COMPANY_ADMIN, pageable)
                .map(UserView::from);
    }
}
