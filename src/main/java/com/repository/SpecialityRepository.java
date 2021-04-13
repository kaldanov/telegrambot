package com.repository;

import com.entity.custom.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {

   List<Speciality> findAllByLangIdAndTypeOrderById(int langId, int type);
   Speciality findAllByIdAndLangIdAndAndType(int id ,int langId, int type);
   List<Speciality> findAllByLangIdAndTypeAndSchoolType(int langId, int type, int schoolType);
}

