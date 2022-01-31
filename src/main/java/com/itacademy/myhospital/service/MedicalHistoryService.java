package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.*;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface MedicalHistoryService {
    List<MedicalHistory> findAll();
    MedicalHistory findById(Integer id) throws MedicalHistoryException;
    void saveAndFlush(MedicalHistory medicalHistory);
    void deleteById(Integer id) throws MedicalHistoryException;
    List<MedicalHistory> findByPatient(Person patient);
    MedicalHistory createAndSaveNewMedicalHistoryFromDto(MedicalHistoryDtoWithProcesses dto);
    /**
     * This method changes status of Medical History, MedicalHistoryProcesses on true and set Discharge Date and save the history
     * @param id - id of Medical History
     * @return Medical History
     * @throws MedicalHistoryException if there is no Medical History with this id in database and
     * if Medical History is already discharged
     */
    boolean dischargePatient(Integer id) throws MedicalHistoryException;
    /**
     * This method checks whether the user is a patient in this medical history or he has enough permissions to view
     * @param historyId - id of MedicalHistory
     * @param username - name of User
     * @return MedicalHistory
     * @throws MedicalHistoryException if there is no MedicalHistory with this id in database
     * @throws UserException if there is no user with the username in database or user doesn't have enough permissions
     */
    MedicalHistory checkPersonForViewHistory(Integer historyId, String username) throws MedicalHistoryException, UserException;
    /**
     * This method returns all medical histories of the patient
     * @param username - name of User
     * @return list of Medical Histories
     * @throws PersonException if there is no person with the user
     */
    List<MedicalHistory> findHistoriesOfPatient(String username) throws PersonException;
    /**
     * @param historyDto - MedicalHistoryDtoWithNumberOfProcesses
     * @param personal - who is a doctor in this MedicalHistory
     * @return MedicalHistoryDtoWithProcesses
     * @throws ProcessException if there is no process with id in database
     */
    MedicalHistoryDtoWithProcesses getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(MedicalHistoryDtoWithNumberOfProcesses historyDto,Person personal) throws ProcessException;
}

