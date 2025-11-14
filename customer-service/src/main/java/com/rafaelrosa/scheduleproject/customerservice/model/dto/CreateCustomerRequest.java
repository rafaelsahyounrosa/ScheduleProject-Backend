package com.rafaelrosa.scheduleproject.customerservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CreateCustomerRequest(@NotBlank String firstName,
                                    @NotBlank String lastName,
                                    @Email @NotBlank String email,
                                    String phone,
                                    String address,
                                    @Past LocalDate birthDate,
                                    Long companyId) {}
