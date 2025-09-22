package com.rafaelrosa.scheduleproject.userservice.service;

import com.rafaelrosa.scheduleproject.commonentities.enums.Roles;
import com.rafaelrosa.scheduleproject.userservice.dto.CreateCollaboratorRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UpdateCollaboratorRequest;
import com.rafaelrosa.scheduleproject.userservice.dto.UserView;
import com.rafaelrosa.scheduleproject.userservice.model.Company;
import com.rafaelrosa.scheduleproject.userservice.model.User;
import com.rafaelrosa.scheduleproject.userservice.repository.CompanyRepository;
import com.rafaelrosa.scheduleproject.userservice.repository.UserRepository;
import com.rafaelrosa.scheduleproject.userservice.security.Authz;
import com.rafaelrosa.scheduleproject.userservice.utils.PasswordUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Slf4j
@Service
public class CollaboratorService {

    private final UserRepository users;
    private final CompanyRepository companies;
    private final Authz authz;

    public CollaboratorService(UserRepository users, CompanyRepository companies, Authz authz) {
        this.users = users;
        this.companies = companies;
        this.authz = authz;
    }

    @Transactional
    public UserView create(CreateCollaboratorRequest req) {

        final boolean admin = authz.isAdmin();
        Long targetCompanyId;

        if (admin){
            targetCompanyId = req.companyId();
        } else {
            targetCompanyId = authz.currentCompanyId();
            //rejeitando request de companyId diferente do token que nao é admin
            if (req.companyId() != null && !req.companyId().equals(targetCompanyId)){
                throw new IllegalArgumentException("You are not allowed to create a collaborator for another company.");
            }
        }


        if (targetCompanyId == null) {
            throw new IllegalArgumentException("Your token has no company scope.");
        }


        Company c = companies.findById(targetCompanyId)
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found."));

        User u = new User();
        u.setUsername(req.username());
        u.setName(req.name());
        u.setEmail(req.email());
        u.setPassword(PasswordUtils.encode(req.password()));
        u.setRole(Roles.COLLABORATOR.name());
        u.setCompany(c);

        return UserView.from(users.save(u));
    }

    @Transactional
    public UserView update(Long id, UpdateCollaboratorRequest req) {

        User u = users.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found."));

        //TODO Remover log
        System.out.println("[ROLE COMPARISON CHECK] Role: " + u.getRole() + " ROLE ENUM: " + Roles.COLLABORATOR.name());
            try {

                if(u.getRole().equals(Roles.COLLABORATOR.name())) {
                throw new AccessDeniedException("You are not allowed to update Collaborator.");
                }

                if(!authz.isAdmin()) {
                    Long cid = (u.getCompany() != null ? u.getCompany().getId() : null);
                    if(!authz.sameCompany(cid)) throw new AccessDeniedException("You are not allowed to update Collaborator.");
                }

            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }

        if (req.name() != null) u.setUsername(req.name());
        if (req.email() != null) u.setEmail(req.email());
        return UserView.from(users.save(u));
    }

    @Transactional
    public void delete(Long id) {

        try {

        User u = users.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found."));

        if(u.getRole().equals(Roles.COLLABORATOR.name())) {
            throw new AccessDeniedException("You are not allowed to delete Collaborator.");
        }

        if(!authz.isAdmin()) {
            Long cid = (u.getCompany() != null ? u.getCompany().getId() : null);
            if(!authz.sameCompany(cid)) throw new AccessDeniedException("You are not allowed to delete Collaborator.");
        }
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        }

        users.deleteById(id);
    }

    @Transactional
    public Page<UserView> list(Pageable pageable) {

        try {
            if (authz.isAdmin()) {
                return users.findAllByRole(Roles.COLLABORATOR, pageable).map(UserView::from);
            }
            Long cid = authz.currentCompanyId();
            if (cid == null) {
                throw new AccessDeniedException("Your token has no company scope.");
            } else {
                return users.findAllByRoleAndCompany_Id(Roles.COLLABORATOR, cid, pageable).map(UserView::from);
            }
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        }
    }

}
