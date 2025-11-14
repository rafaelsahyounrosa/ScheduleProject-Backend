package com.rafaelrosa.scheduleproject.customerservice.model;

import com.rafaelrosa.scheduleproject.commonentities.CustomerDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends CustomerDTO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "first_name", length = 20)
    private String firstName;
    @Column(nullable = false, name = "last_name", length = 50)
    private String lastName;
    @Column(unique = true, length = 50)
    private String email;
    @Column(length = 20, unique = true)
    private String phone;
    @Column(length = 255)
    private String address;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(nullable = false, name = "company_id")
    private Long companyId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public Long getCompanyId() {
        return companyId;
    }
    @Override
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
