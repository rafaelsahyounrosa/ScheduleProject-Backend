package com.rafaelrosa.scheduleproject.customerservice.service;

import com.rafaelrosa.scheduleproject.customerservice.model.Customer;
import com.rafaelrosa.scheduleproject.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void saveCustomer(Customer customer){
        customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id){
        return customerRepository.findById(id).orElse(null);
    }

    public void deleteCustomerById(Long id){
        customerRepository.deleteById(id);
    }

    public Iterable<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public void updateCustomer(Customer customer){
        var dbCustomer = getCustomerById(customer.getId());

        if(dbCustomer != null){
            dbCustomer.setFirstName(customer.getFirstName());
            dbCustomer.setLastName(customer.getLastName());
            dbCustomer.setEmail(customer.getEmail());
            dbCustomer.setPhone(customer.getPhone());
            dbCustomer.setAddress(customer.getAddress());
            saveCustomer(dbCustomer);
        }

    }
}
