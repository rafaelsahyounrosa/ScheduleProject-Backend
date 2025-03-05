package com.rafaelrosa.scheduleproject.schedulingservicecreation.service;

import com.rafaelrosa.scheduleproject.commonentities.CustomerDTO;
import com.rafaelrosa.scheduleproject.commonentities.exceptions.NotFoundException;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.feign.CustomerClient;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import com.rafaelrosa.scheduleproject.schedulingservicecreation.repository.SchedulingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchedulingService {

    private final SchedulingRepository schedulingRepository;
    private final CustomerClient customerClient;

    public SchedulingService(SchedulingRepository schedulingRepository, CustomerClient customerClient) {
        this.schedulingRepository = schedulingRepository;
        this.customerClient = customerClient;
    }

    public Scheduling scheduleCustomer(Scheduling scheduling){

        //TODO implementar validação de companyID também
        //TODO por enquanto está sem uso, mas pode ser que seja legal ter o user completo caso queira mandar algum email ou mensagem de confirmação do agendamento
        CustomerDTO customer = customerClient.getCustomerById(scheduling.getCustomerId());

        if(customer == null){

            throw new NotFoundException("Customer with id: " + scheduling.getCustomerId() + " not found");
        }


        return schedulingRepository.save(scheduling);

    }

    public Iterable<Scheduling> findAll() {
        return schedulingRepository.findAll();
    }

    public Optional<Scheduling> findById(Long id) {

        Optional<Scheduling> scheduling = schedulingRepository.findById(id);

        if (!scheduling.isPresent()) {

            throw new NotFoundException("Scheduling with id: " + id + " not found");
        }

        return scheduling;
    }

    public void deleteById(Long id) {

        if (!schedulingRepository.existsById(id)) {
            throw new NotFoundException("Scheduling with id: " + id + " not found");
        }

        schedulingRepository.deleteById(id);
    }
}
