package com.repository;

import com.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    Message findById(long id);
    Message  findByIdAndLangId(long id, int languageId);

    @Query("select m.name from Message m WHERE m.id =?1 and m.langId =?2")
    String    getMessageText(long id, int langId);

    //Message findByIdAndLangId(int id, int langId);



//    Optional<Message> findById(int id);

//    Message             findByIdAndLangId(int id, int languageId);
}
