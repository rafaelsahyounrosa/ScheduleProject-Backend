package com.rafaelrosa.scheduleproject.customerservice.service;

import com.rafaelrosa.commonsecurity.Authz;
import com.rafaelrosa.scheduleproject.customerservice.model.Customer;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.CreateCustomerRequest;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.CustomerSummary;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.CustomerView;
import com.rafaelrosa.scheduleproject.customerservice.model.dto.UpdateCustomerRequest;
import com.rafaelrosa.scheduleproject.customerservice.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final Authz authz;

    public CustomerService(CustomerRepository customerRepository, Authz authz) {
        this.customerRepository = customerRepository;
        this.authz = authz;
    }

    @Transactional
    public CustomerView saveCustomer(CreateCustomerRequest request){

        final boolean admin = authz.isAdmin();

        Long targetCompanyId = admin ? request.companyId() : authz.currentCompanyId();

        if(targetCompanyId == null){
            throw new AccessDeniedException("Your token has no company scope");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        customer.setDateOfBirth(request.birthDate());
        customer.setCompanyId(targetCompanyId);

        System.out.println("[REPOSITORY]: saveCustomer get Date of Birth]" + customer.getDateOfBirth());

        return CustomerView.from(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerView getCustomerById(Long id){

        if (authz.isAdmin()) {
            return customerRepository.findById(id).map(CustomerView::from).orElse(null);
        }

        Long cid = authz.currentCompanyId();
        if (cid == null) throw new AccessDeniedException("Your token has no company scope");
        //TODO tratar null no response entity com empty ao inves do HTTP 200 ok?
        return customerRepository.findByIdAndCompanyId(id, cid).map(CustomerView::from).orElse(null);
    }

    @Transactional
    public void deleteCustomerById(Long id){

        Customer c = customerRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Customer with id: " + id + " not found"));

        if (!authz.isAdmin()) {
            Long cid = authz.currentCompanyId();
            if(cid == null || !cid.equals(c.getCompanyId())) {
                throw new AccessDeniedException("Your token has no company scope for this user");
            }
        }

        customerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<CustomerView> getAllCustomers(Pageable pageable){

        if(authz.isAdmin()){
            return customerRepository.findAll(pageable).map(CustomerView::from);
        }

        Long cid = authz.currentCompanyId();
        if(cid == null) throw new AccessDeniedException("Your token has no company scope");
        return customerRepository.findAllByCompanyId(cid, pageable).map(CustomerView::from);

    }

    @Transactional
    public CustomerView updateCustomer(Long idURL, UpdateCustomerRequest req){

        Customer c = customerRepository.findById(idURL)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id " + idURL));

        if(!authz.isAdmin()){
            Long cid = authz.currentCompanyId();
            if (cid == null || !cid.equals(c.getCompanyId())){
                throw new AccessDeniedException("Your token has no company scope for this user");
            }
        }

        if(req.firstName() != null) c.setFirstName(req.firstName());
        if(req.lastName() != null) c.setLastName(req.lastName());
        if(req.email() != null) c.setEmail(req.email());
        if(req.phone() != null) c.setPhone(req.phone());
        if(req.address() != null) c.setAddress(req.address());
        if(req.birthDate() != null) c.setDateOfBirth(req.birthDate());

        return CustomerView.from(customerRepository.save(c));
    }

    public Optional<CustomerSummary> findSummaryScoped(Long id) {
        if (authz.isAdmin()) {
            return customerRepository.findById(id).map(CustomerSummary::from);
        }
        Long cid = authz.currentCompanyId();
        if (cid == null) throw new AccessDeniedException("Your token has no company scope");
        if (!cid.equals(id)) throw new AccessDeniedException("Forbidden company");
        return customerRepository.findByIdAndCompanyId(id, cid).map(CustomerSummary::from);
    }
}
