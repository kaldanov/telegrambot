package com.repository;

import com.entity.custom.Onay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OnayRepository extends JpaRepository<Onay, Integer> {

    List<Onay> getAllByDateBetween(Date dateStart, Date dateEnd);
    List<Onay> findAll();

}
