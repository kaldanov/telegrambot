package com.repository;

import com.entity.custom.Recipient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RecipientRepository extends CrudRepository<Recipient, Integer> {

    Recipient findByChatId(long chatId);
    List<Recipient> findAllByOrderById();
    int                 countByChatId(long chatId);
    int                 countByIin(String iin);
    List<Recipient> findAllByRegistrationDateBeforeAndRegistrationDateAfterOrderById(Date start, Date end);
    List<Recipient> findAllByRegistrationDateBetweenOrderById(Date start, Date end);
    Recipient findByIin(String iin);
}
