package com.rafaelrosa.scheduleproject.userservice.service;

import com.rafaelrosa.scheduleproject.userservice.model.Company;
import com.rafaelrosa.scheduleproject.userservice.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Iterable<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Long idUrl ,Company company) {

        Optional<Company> companyOptional = companyRepository.findById(idUrl);
        if(!companyOptional.isPresent()){
            throw new IllegalArgumentException("Company not found");
        }

        companyOptional.get().setName(company.getName());
        companyOptional.get().setDetails(company.getDetails());

        return companyRepository.save(companyOptional.get());
    }

    public void deleteCompany(Long idUrl) {
        Optional<Company> companyOptional = companyRepository.findById(idUrl);
        if(!companyOptional.isPresent()){
            throw new IllegalArgumentException("Company not found");
        }
        companyRepository.delete(companyOptional.get());
    }
}
