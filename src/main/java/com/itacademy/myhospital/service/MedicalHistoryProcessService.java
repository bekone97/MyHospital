package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.Process;

import java.util.List;
import java.util.Map;

public interface MedicalHistoryProcessService {
    List<MedicalHistoryProcess> findAll();
    MedicalHistoryProcess findById(Integer id) throws MedicalHistoryProcessException;
    void saveAndFlush(MedicalHistoryProcess item);
    boolean deleteById(Integer id) throws  MedicalHistoryProcessException;
    /**
     * This method gets list of MedicalHistoryProcess and adds them to new MedicalHistoryDtoWithProcesses,and adds complain
     * ,a patient from historyDto
     * @param mapOfProcesses - where a key is a process and a value is a quantity of this process
     * @param historyDto - HistoryDtoWithNumberOfProcesses with complain, a patient for new HistoryDtoWithProcesses
     * @return new MedicalHistoryDtoWithProcesses
     */
    MedicalHistoryDtoWithProcesses createMedicalHistoryProcessesAndAddToHistory(Map<Process, Integer> mapOfProcesses,
                                                                                       MedicalHistoryDtoWithNumberOfProcesses historyDto);
    /**
     * This method checks the NameOfProcess for MedicalHistoryProcess and saves MedicalHistoryProcess
     * @param medicalHistoryProcess - list of MedicalHistoryProcesses
     * @return checked MedicalHistoryProcess
     */
    MedicalHistoryProcess checkAndSaveMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);
}
