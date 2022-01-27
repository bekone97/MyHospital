package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.repository.MedicalHistoryProcessRepository;
import com.itacademy.myhospital.service.MedicalHistoryProcessService;
import com.itacademy.myhospital.service.NameOfProcessService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class MedicalHistoryProcessServiceImpl implements MedicalHistoryProcessService {

    public static final String NO_MEDICAL_HISTORY_PROCESS_WITH_ID_EXCEPTION = "There is no medical history process with id :";
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
        var optional = medicalHistoryProcessRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new MedicalHistoryProcessException(NO_MEDICAL_HISTORY_PROCESS_WITH_ID_EXCEPTION + id);
        }
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
            throw new MedicalHistoryProcessException(NO_MEDICAL_HISTORY_PROCESS_WITH_ID_EXCEPTION + id);
        }
    }


    //returned history with list of nameOfProcesses
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

    //returned list with required number of MedicalHistoryProcesses. Add to MedicalHistoryProcess nameOfProcess with required Process
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
    public MedicalHistoryProcess checkAndSaveMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess){
        var nameOfProcess=nameOfProcessService.checkIfExist(medicalHistoryProcess.getNameOfProcess());
        medicalHistoryProcess.setNameOfProcess(nameOfProcess);
        saveAndFlush(medicalHistoryProcess);
        return medicalHistoryProcess;
    }


}
