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
    public List<MedicalHistory> findAll();
    public MedicalHistory findById(Integer id) throws MedicalHistoryException;
    public void saveAndFlush(MedicalHistory medicalHistory);
    public void deleteById(Integer id) throws MedicalHistoryException;
//    public List<MedicalHistory> findByReceiptDate(LocalDate receiptDate) throws MedicalHistoryException;
//    public List<MedicalHistory> findByDischargeDate(LocalDate receiptDate) throws MedicalHistoryException;
    public List<MedicalHistory> findByPatient(Person patient);
//    public MedicalHistory findByPatientAndReceiptDate(Person patient,
//                                                      LocalDate receiptDate);
    public MedicalHistory createAndSaveNewMedicalHistoryFromDto(MedicalHistoryDtoWithProcesses dto);
//    public void saveUpdatedMedicalHistoryFromMedicalHistoryDto(MedicalHistoryDtoWithNumberOfProcesses historyDto);
    public boolean dischargePatient(Integer id) throws MedicalHistoryException;
    public MedicalHistory checkPersonForViewHistory(Integer historyId, String username) throws MedicalHistoryException, UserException;
    public List<MedicalHistory> getHistoriesOfPatient(String username) throws PersonException;
    public MedicalHistoryDtoWithProcesses getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(MedicalHistoryDtoWithNumberOfProcesses historyDto,Person personal) throws ProcessException;
}

