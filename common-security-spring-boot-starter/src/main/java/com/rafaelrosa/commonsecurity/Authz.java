package com.rafaelrosa.commonsecurity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
public class Authz {

    public Boolean isAdmin(){
        var a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream()
                .anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
    }

    public Long currentCompanyId(){
        var a = SecurityContextHolder.getContext().getAuthentication();
        if(a == null) return null;
        Object p =  a.getPrincipal();
        if(p instanceof  AuthenticatedUser au) return au.companyId();
        return null;
    }

    public Long requiredCompanyId(){
        Long cid = currentCompanyId();
        if(cid == null) throw new AccessDeniedException("Your token has no company scope");
        return cid;
    }
}
