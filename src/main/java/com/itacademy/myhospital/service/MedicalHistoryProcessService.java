package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.Process;

import java.util.List;
import java.util.Map;

public interface MedicalHistoryProcessService {
    public List<MedicalHistoryProcess> findAll();
    public MedicalHistoryProcess findById(Integer id) throws MedicalHistoryProcessException;
    public void saveAndFlush(MedicalHistoryProcess item);
    public boolean deleteById(Integer id) throws  MedicalHistoryProcessException;
    public MedicalHistoryDtoWithProcesses createMedicalHistoryProcessesAndAddToHistory(Map<Process, Integer> mapOfProcesses,
                                                                                       MedicalHistoryDtoWithNumberOfProcesses historyDto);
    public MedicalHistoryProcess checkAndSaveMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);
}
