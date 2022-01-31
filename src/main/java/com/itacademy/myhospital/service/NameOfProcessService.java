package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.NameOfProcessesException;
import com.itacademy.myhospital.model.entity.NameOfProcess;

import java.util.List;

public interface NameOfProcessService {
    List<NameOfProcess> findAll();
    NameOfProcess findById(Integer id) throws NameOfProcessesException;
    void saveAndFlush(NameOfProcess item);
    void deleteById(Integer id) throws  NameOfProcessesException;
    List<NameOfProcess> findByName(String name);
    /**
     * This method checks name of processes. If a name of process exists, the method adds existing Name Of Process from
     * database,if not adds name of process from dto
     * @param dto - MedicalHistoryDtoWithProcesses
     * @return MedicalHistoryDtoWithProcesses with changed name of processes
     */
    MedicalHistoryDtoWithProcesses checkNameOfProcesses(MedicalHistoryDtoWithProcesses dto);
    /**
     * This method checks for the presence of a class in the database, and if it is, the method returns NameOfProcess
     * from database, or returns NameOfProcess from controller
     * @param nameOfProcess - nameOfProcess from controller
     * @return checked nameOfProcess
     */
    NameOfProcess checkIfExist(NameOfProcess nameOfProcess);
}
