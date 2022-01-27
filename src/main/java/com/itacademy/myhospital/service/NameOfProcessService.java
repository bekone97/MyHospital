package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.DiagnosisException;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.exception.NameOfProcessesException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;


import java.util.List;

public interface NameOfProcessService {
    public List<NameOfProcess> findAll();
    public NameOfProcess findById(Integer id) throws NameOfProcessesException;
    public void saveAndFlush(NameOfProcess item);
    public void deleteById(Integer id) throws  NameOfProcessesException;
    public List<NameOfProcess> findByName(String name);
    public MedicalHistoryDtoWithProcesses checkNameOfProcesses(MedicalHistoryDtoWithProcesses dto);
    public NameOfProcess checkIfExist(NameOfProcess nameOfProcess);
}
