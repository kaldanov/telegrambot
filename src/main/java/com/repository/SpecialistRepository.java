package com.repository;


import com.entity.custom.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Integer> {
    List<Specialist> findAllByChatIdOrderById(long chatId);
    List<Specialist> findAllByOrderById();


    Specialist findById(long chatId);
}
