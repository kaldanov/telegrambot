package com.repository;

import com.entity.custom.ComesCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ComesCourseRepository extends JpaRepository<ComesCourse,Integer> {

//  List<ComesCourse> findAllByRegistrationServiceIdAndActionDateAfterAndActionDateBefore(long regServiceId, Date start, Date end);
    List<ComesCourse> findAllByRegistrationServiceIdOrderById(long regServiceId);
    List<ComesCourse> findAllByRegistrationServiceIdAndActionDateBetweenOrderById(long regServiceId, Date start, Date end);
}
