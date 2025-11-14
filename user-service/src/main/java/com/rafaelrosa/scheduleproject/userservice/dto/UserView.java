package com.rafaelrosa.scheduleproject.userservice.dto;

import com.rafaelrosa.scheduleproject.userservice.model.User;

public record UserView(Long id,String username, String name, String email, String role, Long companyId) {

    public static UserView from(User u){
        Long cid = (u.getCompany() != null ? u.getCompany().getId() : null);
        return new UserView(u.getId(), u.getUsername(), u.getName(), u.getEmail(), u.getRole(), cid);
    }
}
