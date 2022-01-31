package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.NameOfProcessesException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;
import com.itacademy.myhospital.model.repository.NameOfProcessRepository;
import com.itacademy.myhospital.service.NameOfProcessService;
import org.springframework.stereotype.Service;
import static com.itacademy.myhospital.constants.Constants.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class NameOfProcessServiceImpl implements NameOfProcessService {

    private final NameOfProcessRepository nameOfProcessRepository;

    public NameOfProcessServiceImpl(NameOfProcessRepository nameOfProcessRepository) {
        this.nameOfProcessRepository = nameOfProcessRepository;
    }

    @Override
    public List<NameOfProcess> findAll() {
        return nameOfProcessRepository.findAll();
    }

    @Override
    public NameOfProcess findById(Integer id) throws NameOfProcessesException {
        return nameOfProcessRepository.findById(id)
                .orElseThrow(()->new NameOfProcessesException(NO_NAME_OF_PROCESS_EXCEPTION + id));
    }

    @Override
    @Transactional
    public void saveAndFlush(NameOfProcess item) {
        nameOfProcessRepository.saveAndFlush(item);
    }

    @Override
    public void deleteById(Integer id) throws NameOfProcessesException {
        if (nameOfProcessRepository.existsById(id)) {
            nameOfProcessRepository.deleteById(id);
        }else {
            throw new NameOfProcessesException(NO_NAME_OF_PROCESS_EXCEPTION +id);
        }
    }

    @Override
    public List<NameOfProcess> findByName(String name) {
        return nameOfProcessRepository.findByName(name);
    }


    public MedicalHistoryDtoWithProcesses checkNameOfProcesses(MedicalHistoryDtoWithProcesses dto) {
        List<MedicalHistoryProcess> medicalHistoryProcesses = new ArrayList<>();
        for (MedicalHistoryProcess medicalHistoryProcess :
                dto.getMedicalHistoryProcesses()) {
            var nameOfProcess =checkIfExist(medicalHistoryProcess.getNameOfProcess());
            medicalHistoryProcess.setNameOfProcess(nameOfProcess);
            medicalHistoryProcesses.add(medicalHistoryProcess);
        }
        dto.setMedicalHistoryProcesses(medicalHistoryProcesses);
        return dto;
    }


    public NameOfProcess checkIfExist(NameOfProcess nameOfProcess) {
        var maybeNameOfProcesses = findByName(nameOfProcess.getName());
        if (!maybeNameOfProcesses.isEmpty()) {
            return maybeNameOfProcesses.stream().findAny().get();
        } else {
            return nameOfProcess;
        }
    }
}
