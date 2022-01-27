package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.*;
import com.itacademy.myhospital.model.repository.MedicalHistoryRepository;
import com.itacademy.myhospital.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    public static final String NO_HISTORY_EXCEPTION = "Medical history doesn't exist with id: ";
    public static final String NO_MEDICAL_HISTORIES_EXCEPTION = "There are no medical histories in the project";
    public static final String ROLE_NURSE = "ROLE_NURSE";
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final UserService userService;
    private final PersonService personService;
    private final DiagnosisService diagnosisService;
    private final ProcessService processService;
    private final MedicalHistoryProcessService medicalHistoryProcessService;


    @Override
    public List<MedicalHistory> findAll() {
        return medicalHistoryRepository.findAll();
    }

    @Override
    public MedicalHistory findById(Integer id) throws MedicalHistoryException {
        MedicalHistory medicalHistory = null;
        var optional = medicalHistoryRepository.findById(id);
        if(optional.isPresent()) {
            medicalHistory = optional.get();
        }else {
            throw new MedicalHistoryException(NO_HISTORY_EXCEPTION + id);
        }
        return medicalHistory;
    }

    @Override
    public void saveAndFlush(MedicalHistory item) {
        medicalHistoryRepository.saveAndFlush(item);
    }

    @Override
    public void deleteById(Integer id) throws MedicalHistoryException {
        if (medicalHistoryRepository.existsById(id)) {
            medicalHistoryRepository.deleteById(id);
        }else {
            throw new MedicalHistoryException(NO_HISTORY_EXCEPTION);
        }
    }

//    @Override
//    public List<MedicalHistory> findByReceiptDate(LocalDate receiptDate) throws MedicalHistoryException {
//        var histories= medicalHistoryRepository.findByReceiptDate(receiptDate);
//        if (histories==null){
//            throw new MedicalHistoryException(NO_MEDICAL_HISTORIES_EXCEPTION);
//        }
//        return histories;
//    }

//    @Override
//    public List<MedicalHistory> findByDischargeDate(LocalDate dischargeDate) throws MedicalHistoryException {
//        var histories= medicalHistoryRepository.findByDischargeDate(dischargeDate);
//        if (histories==null){
//            throw new MedicalHistoryException(NO_MEDICAL_HISTORIES_EXCEPTION);
//        }
//        return histories;
//    }

    @Override
    public List<MedicalHistory> findByPatient(Person patient){
        return  medicalHistoryRepository.findByPatient(patient);
    }



//    @Override
//    public MedicalHistory findByPatientAndReceiptDate(Person patient,
//                                                      LocalDate receiptDate) {
//        return medicalHistoryRepository.findByPatientAndReceiptDate(patient,
//                                                                     receiptDate);
//    }
//    Change status of processes of medical history and medical history on true and set Discharge Date and save history
    @Override
    public boolean dischargePatient(Integer id) throws MedicalHistoryException {
        var medicalHistory =findById(id);
        if (!medicalHistory.isStatus()){
        medicalHistory.setDischargeDate(new Timestamp(System.currentTimeMillis()));
        medicalHistory.getMedicalHistoryProcesses()
                .forEach(process->process.setStatus(true));
        medicalHistory.setStatus(true);
        saveAndFlush(medicalHistory);
        return true;
        }else {
            throw new MedicalHistoryException("Medical history has already discharged");
        }
    }
//check person: Is he  a patient of the history, or maybe he has role ROLE_NURSE, And check history. If is it true return history, is it false - null
    @Override
    public MedicalHistory checkPersonForViewHistory(Integer historyId, String username) throws MedicalHistoryException, UserException {
        var history = findById(historyId);
        var currentUser = userService.findByUsername(username);
        if (currentUser != null) {
            boolean hasRoleNurse = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getName().equals(ROLE_NURSE));

            var currentPerson = personService.findByUser(currentUser);
            if ((currentPerson == history.getPatient() || hasRoleNurse)) {
                return history;
            }
            throw new UserException("User doesn't have enough permissions");
        } else {
            throw new UserException("User doesn't exist with name " + username);
        }
    }

    public MedicalHistory createAndSaveNewMedicalHistoryFromDto(MedicalHistoryDtoWithProcesses dto) {

        var history = MedicalHistory.builder()
                .patient(dto.getPatient())
                .receiptDate(new Timestamp(System.currentTimeMillis()))
                .complain(dto.getComplain())
                .diagnosis(Diagnosis.builder()
                        .name(dto.getDiagnosis().getName())
                        .personal(dto.getDiagnosis().getPersonal())
                        .build())
                .medicalHistoryProcesses(dto.getMedicalHistoryProcesses())
                .build();
        for (MedicalHistoryProcess process:
                history.getMedicalHistoryProcesses()) {
            process.setMedicalHistory(history);
        }
        saveAndFlush(history);
        return history;
    }
    public List<MedicalHistory> getHistoriesOfPatient(String username) throws PersonException {
        var person = personService.findPersonByUsernameOfUser(username);
        if (person!=null) {
            return  findByPatient(person);
        }else {
            throw new PersonException("User doesn't have a person");
        }
    }

    //make tests and description
    public MedicalHistoryDtoWithProcesses getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(MedicalHistoryDtoWithNumberOfProcesses historyDto,Person personal) throws ProcessException {
        var diagnosis = diagnosisService.findOrCreateDiagnosis(historyDto.getDiagnosis().getName(), personal);
        var mapOfProcesses = processService.getMapOfProcesses(historyDto.getNumberOfOperations(),
                historyDto.getNumberOfProcedures(),
                historyDto.getNumberOfMedications());
        var dto = medicalHistoryProcessService.createMedicalHistoryProcessesAndAddToHistory(mapOfProcesses,
                historyDto);
        dto.setDiagnosis(diagnosis);
        return dto;
    }
//    public void saveUpdatedMedicalHistoryFromMedicalHistoryDto(MedicalHistoryDtoWithNumberOfProcesses historyDto) {
//        MedicalHistory medicalHistory = MedicalHistory.builder()
//                .id(historyDto.getId())
//                .receiptDate(Timestamp.valueOf(LocalDateTime.now()))
//                .dischargeDate(checkDtoDate(historyDto))
//                .patient(historyDto.getPatient())
//                .build();
//        saveAndFlush(medicalHistory);
//    }
//
//    private Timestamp checkDtoDate(MedicalHistoryDtoWithNumberOfProcesses dto) {
//            return null;
//    }
}
