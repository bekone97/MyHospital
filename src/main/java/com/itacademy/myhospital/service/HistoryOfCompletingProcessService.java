package com.itacademy.myhospital.service;


import com.itacademy.myhospital.exception.HistoryOfCompletingProcessException;
import com.itacademy.myhospital.model.entity.HistoryOfCompletingProcess;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.Person;

import java.util.List;

public interface HistoryOfCompletingProcessService {
     List<HistoryOfCompletingProcess> findAll();
     HistoryOfCompletingProcess findById(Integer id) throws HistoryOfCompletingProcessException;
     void saveAndFlush(HistoryOfCompletingProcess item);
     void deleteById(Integer id) throws HistoryOfCompletingProcessException;

     List<HistoryOfCompletingProcess> findByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);
     boolean checkNumberOfExecutionsAndCreateNewExecution(MedicalHistoryProcess medicalHistoryProcess,
                                                                                    Person person,
                                                                                   String result) throws HistoryOfCompletingProcessException;
     public boolean removeHistoryOfCompletingProcessesByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);
//     List<HistoryOfCompletingProcess> findByMedicalHistoryId(Integer medicalHistoryId) throws HistoryOfCompletingProcessException;

}
