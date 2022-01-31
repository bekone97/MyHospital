package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis,Integer> {

    List<Diagnosis> findByNameAndPersonal(String name,Person personal);
    List<Diagnosis> findByPersonal(Person personal);
}
