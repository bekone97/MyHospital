package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.repository.MedicalHistoryProcessRepository;
import com.itacademy.myhospital.service.MedicalHistoryProcessService;
import com.itacademy.myhospital.service.NameOfProcessService;
import org.springframework.stereotype.Service;
import static com.itacademy.myhospital.constants.Constants.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MedicalHistoryProcessServiceImpl implements MedicalHistoryProcessService {


    private final MedicalHistoryProcessRepository medicalHistoryProcessRepository;
    private final NameOfProcessService nameOfProcessService;

    public MedicalHistoryProcessServiceImpl(MedicalHistoryProcessRepository medicalHistoryProcessRepository,
                                            NameOfProcessService nameOfProcessService) {
        this.medicalHistoryProcessRepository = medicalHistoryProcessRepository;
        this.nameOfProcessService = nameOfProcessService;
    }

    @Override
    public List<MedicalHistoryProcess> findAll() {
        return medicalHistoryProcessRepository.findAll();
    }

    @Override
    public MedicalHistoryProcess findById(Integer id) throws MedicalHistoryProcessException {
        return medicalHistoryProcessRepository.findById(id)
                .orElseThrow(()->new MedicalHistoryProcessException(NO_MEDICAL_HISTORY_EXCEPTION+ id));
    }

    @Override
    public void saveAndFlush(MedicalHistoryProcess item) {
        medicalHistoryProcessRepository.saveAndFlush(item);
    }

    @Override
    public boolean deleteById(Integer id) throws MedicalHistoryProcessException {
        if (medicalHistoryProcessRepository.existsById(id)) {
            medicalHistoryProcessRepository.deleteById(id);
            return true;
        } else {
            throw new MedicalHistoryProcessException(NO_MEDICAL_HISTORY_EXCEPTION + id);
        }
    }



    @Override
    public MedicalHistoryDtoWithProcesses createMedicalHistoryProcessesAndAddToHistory(Map<Process, Integer> mapOfProcesses,
                                                                                       MedicalHistoryDtoWithNumberOfProcesses historyDto) {

        var processes = createAndAddMedicalHistoryProcessesToList(mapOfProcesses);
        return MedicalHistoryDtoWithProcesses.builder()
                .medicalHistoryProcesses(processes)
                .complain(historyDto.getComplain())
                .patient(historyDto.getPatient())
                .build();
    }


    /**
     * This method creates a list of MedicalHistoryProcesses with the required number of name of processes
     * and the necessary processes in them
     * @param mapOfProcesses -  - where a key is a process and a value is a quantity of this process
     * @return list of MedicalHistoryProcesses
     */
    private List<MedicalHistoryProcess> createAndAddMedicalHistoryProcessesToList(Map<Process, Integer> mapOfProcesses) {
        List<MedicalHistoryProcess> medicalHistoryProcesses = new ArrayList<>();
        for (Map.Entry<Process, Integer> entry : mapOfProcesses.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                var medicalHistoryProcess = new MedicalHistoryProcess();
                medicalHistoryProcess.setStatus(false);
                medicalHistoryProcess.setNameOfProcess(new NameOfProcess(entry.getKey()));
                medicalHistoryProcesses.add(medicalHistoryProcess);
            }
        }
        return medicalHistoryProcesses;
    }


    @Transactional
    public MedicalHistoryProcess checkAndSaveMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess){
        var nameOfProcess=nameOfProcessService.checkIfExist(medicalHistoryProcess.getNameOfProcess());
        medicalHistoryProcess.setNameOfProcess(nameOfProcess);
        saveAndFlush(medicalHistoryProcess);
        return medicalHistoryProcess;
    }


}
