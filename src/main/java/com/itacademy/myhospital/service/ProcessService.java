package com.itacademy.myhospital.service;

import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.model.entity.Process;

import java.util.List;
import java.util.Map;

public interface ProcessService{
    public List<Process> findAll();
    public Process findById(Integer id) throws  ProcessException;
    public void saveAndFlush(Process item);
    public void deleteById(Integer id) throws  ProcessException;
    public Map<Process, Integer> getMapOfProcesses(int numberOfOperations,
                                                   int numberOfProcedures,
                                                   int numberOfMedications) throws ProcessException;
}
