package com.repository;


import com.entity.custom.RegistrationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationEventRepository extends JpaRepository<RegistrationEvent, Integer> {
    Boolean findByChatIdAndEventIdOrderById(long chatId, long eventId);
}
