package com.rafaelrosa.scheduleproject.schedulingservicecreation.repository;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulingRepository extends CrudRepository<Scheduling, Long> {
}
