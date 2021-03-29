package com.repository;


import com.entity.custom.ReminderTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderTaskRepository extends JpaRepository<ReminderTask, Integer> {
    ReminderTask findById(long id);
}
