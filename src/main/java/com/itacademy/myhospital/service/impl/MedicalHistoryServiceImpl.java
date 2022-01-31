package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.*;
import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.MedicalHistory;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.repository.MedicalHistoryRepository;
import com.itacademy.myhospital.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.itacademy.myhospital.constants.Constants.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {


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
        return medicalHistoryRepository.findById(id)
                .orElseThrow(()->new MedicalHistoryException(NO_MEDICAL_HISTORY_EXCEPTION+ id));
    }

    @Override
    @Transactional
    public void saveAndFlush(MedicalHistory item) {
        medicalHistoryRepository.saveAndFlush(item);
    }

    @Override
    public void deleteById(Integer id) throws MedicalHistoryException {
        if (medicalHistoryRepository.existsById(id)) {
            medicalHistoryRepository.deleteById(id);
        }else {
            throw new MedicalHistoryException(NO_MEDICAL_HISTORY_EXCEPTION);
        }
    }


    @Override
    public List<MedicalHistory> findByPatient(Person patient){
        return  medicalHistoryRepository.findByPatient(patient);
    }




    @Override
    @Transactional
    public boolean dischargePatient(Integer id) throws MedicalHistoryException {
        var medicalHistory =findById(id);
        if (!medicalHistory.isDischargeStatus()){
        medicalHistory.setDischargeDate(new Timestamp(System.currentTimeMillis()));
        medicalHistory.getMedicalHistoryProcesses()
                .forEach(process->process.setStatus(true));
        medicalHistory.setDischargeStatus(true);
        saveAndFlush(medicalHistory);
        return true;
        }else {
            throw new MedicalHistoryException(MEDICAL_HISTORY_HAS_ALREADY_DISCHARGED_EXCEPTION);
        }
    }


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
            throw new UserException(USER_HAS_NO_PERMISSIONS);
        } else {
            throw new UserException(NO_USER_WITH_USERNAME_EXCEPTION + username);
        }
    }

    @Transactional
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


    public List<MedicalHistory> findHistoriesOfPatient(String username) throws PersonException {
        var person = personService.findPersonByUsernameOfUser(username);
        if (person!=null) {
            return  findByPatient(person);
        }else {
            throw new PersonException(USER_WITHOUT_A_PERSON_EXCEPTION);
        }
    }


    public MedicalHistoryDtoWithProcesses getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(MedicalHistoryDtoWithNumberOfProcesses historyDto,
                                                                                              Person personal) throws ProcessException {
        var diagnosis = diagnosisService.findOrCreateDiagnosis(historyDto.getDiagnosis().getName(), personal);
        var mapOfProcesses = processService.getMapOfProcesses(historyDto.getNumberOfOperations(),
                historyDto.getNumberOfProcedures(),
                historyDto.getNumberOfMedications());
        var dto = medicalHistoryProcessService.createMedicalHistoryProcessesAndAddToHistory(mapOfProcesses,
                historyDto);
        dto.setDiagnosis(diagnosis);
        return dto;
    }
}
