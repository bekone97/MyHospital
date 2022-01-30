package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.NameOfProcessesException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;
import com.itacademy.myhospital.model.repository.NameOfProcessRepository;
import com.itacademy.myhospital.service.NameOfProcessService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class NameOfProcessServiceImpl implements NameOfProcessService {
    public static final String NO_NAME_OF_PROCESS_WITH_ID = "There is no name of process with id ";
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
        var optional = nameOfProcessRepository.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }else {
            throw new NameOfProcessesException(NO_NAME_OF_PROCESS_WITH_ID + id);
        }
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
            throw new NameOfProcessesException(NO_NAME_OF_PROCESS_WITH_ID+id);
        }
    }

    @Override
    public List<NameOfProcess> findByName(String name) {
        return nameOfProcessRepository.findByName(name);
    }

    /**
     * This method checks name of processes. If a name of process exists, the method adds existing Name Of Process from
     * database,if not adds name of process from dto
     * @param dto - MedicalHistoryDtoWithProcesses
     * @return MedicalHistoryDtoWithProcesses with changed name of processes
     */
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
