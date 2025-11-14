package com.rafaelrosa.scheduleproject.userservice.dto;

public record CreateCollaboratorRequest(String username,
                                        String password,
                                        String name,
                                        String email,
                                        Long companyId) {}
