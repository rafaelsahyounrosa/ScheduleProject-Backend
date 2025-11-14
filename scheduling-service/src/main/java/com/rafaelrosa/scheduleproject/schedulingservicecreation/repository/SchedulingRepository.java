package com.rafaelrosa.scheduleproject.schedulingservicecreation.repository;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchedulingRepository extends CrudRepository<Scheduling, Long> {

    Page<Scheduling> findAllByCompanyId(Long companyId, Pageable pageable);

    @Query(value = "select * from schedulings where id = :id and company_id = :companyId",
            nativeQuery = true)
    Optional<Scheduling> findByIdAndCompanyId(@Param("id") Long id, @Param("companyId") Long companyId);

    Page<Scheduling> findAll(Pageable pageable);
}
