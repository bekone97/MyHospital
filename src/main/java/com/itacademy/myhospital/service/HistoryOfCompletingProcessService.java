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
     /**
      * This method checks the MedicalHistoryProcess and creates  a new HistoryOfCompletingProcess with the personal, the result,the date of
      * execution,and with the MedicalHistoryProcess
      * @param medicalHistoryProcess - what process was executed
      * @param person - by whom the process was executed
      * @param result - execution result
      * @return true
      * @throws HistoryOfCompletingProcessException if the status of MedicalHistoryProcess is true or if the number
      * of executed processes equal or more than the number of required executions
      */
     boolean checkNumberOfExecutionsAndCreateNewExecution(MedicalHistoryProcess medicalHistoryProcess,
                                                                                    Person person,
                                                                                   String result) throws HistoryOfCompletingProcessException;
     boolean removeHistoryOfCompletingProcessesByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);

}
