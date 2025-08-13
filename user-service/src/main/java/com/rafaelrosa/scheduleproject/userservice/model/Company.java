package com.rafaelrosa.scheduleproject.userservice.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelrosa.scheduleproject.userservice.converter.CompanyDetailsConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Convert(converter = CompanyDetailsConverter.class)
    @Column(columnDefinition = "JSON")
    private CompanyDetails details;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompanyDetails getDetails() {
        return details;
    }

    public void setDetails(CompanyDetails details) {
        this.details = details;
    }
}