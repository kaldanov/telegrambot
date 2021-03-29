package com.repository;

import com.entity.LanguageUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageUserRepository extends CrudRepository<LanguageUser, Integer> {

    Optional<LanguageUser> findByChatId(long chatId);
}
