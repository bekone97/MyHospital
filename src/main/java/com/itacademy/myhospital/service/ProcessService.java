package com.itacademy.myhospital.service;

import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.model.entity.Process;

import java.util.List;
import java.util.Map;

public interface ProcessService{
    List<Process> findAll();
    Process findById(Integer id) throws  ProcessException;
    void saveAndFlush(Process item);
    void deleteById(Integer id) throws  ProcessException;
    /**
     * This method creates a map where a key is a process and a value is a quantity of it
     * @param numberOfOperations  -  shows the number of operations
     * @param numberOfProcedures -  shows the number of procedures
     * @param numberOfMedications -  shows the number of medications
     * @return Map<Process,Integer>
     * @throws ProcessException if there is no process with the id
     */
    Map<Process, Integer> getMapOfProcesses(int numberOfOperations,
                                                   int numberOfProcedures,
                                                   int numberOfMedications) throws ProcessException;
}
