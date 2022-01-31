package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory,Integer> {

    List<MedicalHistory> findByPatient(Person patient);


}
