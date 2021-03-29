package com.repository;

import com.entity.Button;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ButtonRepository extends JpaRepository<Button, Long> {

    Button findByNameAndLangId(String buttonName, int languageId);


    Button findByIdAndLangId(long buttonId, int languageId);
//    Button findByIdAndLangId(long id);

    @Query("select b.name from Button b where b.id =?1 and b.langId =?2")
    String getButtonText(long id, int languageId);

    @Transactional
    @Modifying
    @Query("update Button set name = ?1, url = ?2 where id = ?3 and langId = ?4")
    void update(String name, String url, long id, int langId);

    Long countByNameAndLangId(String name, int langId);

    Optional<Button> findByName(String buttonId);
}
