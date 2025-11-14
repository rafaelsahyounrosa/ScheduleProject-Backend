package com.rafaelrosa.scheduleproject.userservice.security;

import com.rafaelrosa.scheduleproject.userservice.dto.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
public class Authz {

    private Authentication auth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isAdmin(){
        return auth() != null && auth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public Long currentCompanyId(){

        var a = auth();

        if(a == null) return null;
        Object p = a.getPrincipal();
        if(p instanceof AuthenticatedUser au){
            return au.companyId();
        }
        return null;
    }

    public boolean sameCompany(Long companyId){
        Long currentCompanyId = currentCompanyId();
        return currentCompanyId != null && currentCompanyId.equals(companyId);
    }
}
