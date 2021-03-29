package com.repository;

import com.entity.custom.Services_Spec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
ServicesSpecsRepository extends JpaRepository<Services_Spec,Integer> {

    List<Services_Spec> findAllByServiceIdOrderById(long serviceId);

    List<Services_Spec> findAllBySpecIdOrderById(long chatId);

    Services_Spec findById(long id);

    Services_Spec findByServiceIdAndSpecId(long serviceId, long specId);


}
