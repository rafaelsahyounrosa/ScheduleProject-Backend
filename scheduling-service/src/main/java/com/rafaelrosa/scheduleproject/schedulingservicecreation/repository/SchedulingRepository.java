package com.rafaelrosa.scheduleproject.schedulingservicecreation.repository;

import com.rafaelrosa.scheduleproject.schedulingservicecreation.model.Scheduling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchedulingRepository extends CrudRepository<Scheduling, Long> {

    Page<Scheduling> findAllByCompanyId(Long companyId, Pageable pageable);

    @Query(value = "select * from schedulings where id = :id and company_id = :companyId",
            nativeQuery = true)
    Optional<Scheduling> findByIdAndCompanyId(@Param("id") Long id, @Param("companyId") Long companyId);

    Page<Scheduling> findAll(Pageable pageable);

    @Query("""
        select s from Scheduling s
        where
            lower(s.description) like lower(concat('%', :search, '%'))
         or lower(str(s.status))  like lower(concat('%', :search, '%'))
    """)
    Page<Scheduling> searchGlobalLocal(@Param("search") String search, Pageable pageable);

    @Query("""
        select s from Scheduling s
        where s.companyId = :companyId
          and (
                lower(s.description) like lower(concat('%', :search, '%'))
             or lower(str(s.status))  like lower(concat('%', :search, '%'))
          )
    """)
    Page<Scheduling> searchByCompanyLocal(
            @Param("companyId") Long companyId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
    select s from Scheduling s
    where
        lower(s.description) like lower(concat('%', :search, '%'))
     or lower(str(s.status)) like lower(concat('%', :search, '%'))
     or s.customerId in :customerIds
""")
    Page<Scheduling> searchGlobal(@Param("search") String search,
                                  @Param("customerIds") List<Long> customerIds,
                                  Pageable pageable);

    @Query("""
    select s from Scheduling s
    where s.companyId = :companyId
      and (
            lower(s.description) like lower(concat('%', :search, '%'))
         or lower(str(s.status)) like lower(concat('%', :search, '%'))
         or s.customerId in :customerIds
      )
""")
    Page<Scheduling> searchByCompany(@Param("companyId") Long companyId,
                                     @Param("search") String search,
                                     @Param("customerIds") List<Long> customerIds,
                                     Pageable pageable);
}
