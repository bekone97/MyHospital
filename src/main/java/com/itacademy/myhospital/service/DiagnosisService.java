package com.itacademy.myhospital.service;

import com.itacademy.myhospital.exception.DiagnosisException;
import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.Person;

import java.util.List;

public interface DiagnosisService {
    List<Diagnosis> findAll();
    Diagnosis findById(Integer id) throws DiagnosisException;
    void saveAndFlush(Diagnosis item);
    void deleteById(Integer id) throws DiagnosisException;
    List<Diagnosis> findByNameAndPersonal(String name, Person personal);
    List<Diagnosis> findByPersonal(Person personal);
    Diagnosis findOrCreateDiagnosis(String diagnosisName, Person personal);
    List<Diagnosis> getDiagnosesOfPerson(String username);

}
