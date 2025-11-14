package com.rafaelrosa.scheduleproject.customerservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateCustomerRequest(String firstName,
                                    String lastName,
                                    @Email String email,
                                    String phone,
                                    String address,
                                    @Past LocalDate birthDate) {}
