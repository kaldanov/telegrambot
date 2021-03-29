package com.repository;

import com.entity.Keyboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyboardRepository extends JpaRepository<Keyboard, Integer> {
    Optional<Keyboard> findById(long keyboardId);
//    Keyboard findById(int keyboardId);

}
