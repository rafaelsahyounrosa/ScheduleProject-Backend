package com.rafaelrosa.scheduleproject.userservice.model;

import com.rafaelrosa.scheduleproject.commonentities.CompanyDTO;
import com.rafaelrosa.scheduleproject.userservice.converter.CompanyDetailsConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "companies")
public class Company extends CompanyDTO {

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