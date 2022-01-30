package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.MedicalHistory;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.repository.MedicalHistoryRepository;
import com.itacademy.myhospital.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    public static final String NO_HISTORY_EXCEPTION = "Medical history doesn't exist with id: ";
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
        var optional = medicalHistoryRepository.findById(id);
        if(optional.isPresent()) {
        return optional.get();
        }else {
            throw new MedicalHistoryException(NO_HISTORY_EXCEPTION + id);
        }
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
            throw new MedicalHistoryException(NO_HISTORY_EXCEPTION);
        }
    }


    @Override
    public List<MedicalHistory> findByPatient(Person patient){
        return  medicalHistoryRepository.findByPatient(patient);
    }


    /**
     * This method changes status of Medical History, MedicalHistoryProcesses on true and set Discharge Date and save the history
     * @param id - id of Medical History
     * @return Medical History
     * @throws MedicalHistoryException if there is no Medical History with this id in database and
     * if Medical History is already discharged
     */
//    Change status of processes of medical history and medical history on true and set Discharge Date and save history
    @Override
    @Transactional
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

    /**
     * This method checks whether the user is a patient in this medical history or he has enough permissions to view
     * @param historyId - id of MedicalHistory
     * @param username - name of User
     * @return MedicalHistory
     * @throws MedicalHistoryException if there is no MedicalHistory with this id in database
     * @throws UserException if there is no user with the username in database or user doesn't have enough permissions
     */
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

    /**
     * This method returns all medical histories of the patient
     * @param username - name of User
     * @return list of Medical Histories
     * @throws PersonException if there is no person with the user
     */
    public List<MedicalHistory> getHistoriesOfPatient(String username) throws PersonException {
        var person = personService.findPersonByUsernameOfUser(username);
        if (person!=null) {
            return  findByPatient(person);
        }else {
            throw new PersonException("User doesn't have a person");
        }
    }

    /**
     *This method returns a  MedicalHistoryDtoWithProcesses with added MedicalHistoryProcesses ,diagnosis ,complain,patient
     * @param historyDto - MedicalHistoryDtoWithNumberOfProcesses
     * @param personal - who is a doctor in this MedicalHistory
     * @return MedicalHistoryDtoWithProcesses
     * @throws ProcessException if there is no process with id in database
     */
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
}
