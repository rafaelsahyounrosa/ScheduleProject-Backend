package com.rafaelrosa.scheduleproject.customerservice.service;

import com.rafaelrosa.scheduleproject.customerservice.model.Customer;
import com.rafaelrosa.scheduleproject.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer saveCustomer(Customer customer){
        return customerRepository.save(customer);
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

    public Customer updateCustomer(Long idURL, Customer customer){

        //TODO talvez com o json validation required eu consiga tirar esse if
        if(idURL == null){
            throw new IllegalArgumentException("Customer id cannot be null");
        }

        Optional<Customer> dbCustomer = customerRepository.findById(idURL);
        if(!dbCustomer.isPresent()){
            throw new IllegalArgumentException("Customer not found");
        }

        Customer updatedCustomer = dbCustomer.get();
        updatedCustomer.setFirstName(customer.getFirstName());
        updatedCustomer.setLastName(customer.getLastName());
        updatedCustomer.setEmail(customer.getEmail());
        updatedCustomer.setPhone(customer.getPhone());
        updatedCustomer.setAddress(customer.getAddress());
        updatedCustomer.setDateOfBirth(customer.getDateOfBirth());

        return customerRepository.save(updatedCustomer);
    }
}
