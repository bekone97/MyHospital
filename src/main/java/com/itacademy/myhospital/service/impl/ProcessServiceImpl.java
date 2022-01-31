package com.itacademy.myhospital.service.impl;


import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.repository.ProcessRepository;
import com.itacademy.myhospital.service.ProcessService;
import org.springframework.stereotype.Service;
import static com.itacademy.myhospital.constants.Constants.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository processRepository;


    public ProcessServiceImpl(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Override
    public List<Process> findAll() {
        return processRepository.findAll();
    }

    @Override
    public Process findById(Integer id) throws ProcessException {
        return processRepository.findById(id)
                .orElseThrow(()->new ProcessException(NO_PROCESS_WITH_ID_EXCEPTION +id));
    }

    @Override
    public void saveAndFlush(Process item) {
        processRepository.saveAndFlush(item);
    }

    @Override
    public void deleteById(Integer id) throws ProcessException {
        if (processRepository.existsById(id)) {
            processRepository.deleteById(id);
        }else {
            throw new ProcessException(NO_PROCESS_WITH_ID_EXCEPTION +id);
        }
    }



    @Override
    public Map<Process , Integer> getMapOfProcesses(int numberOfOperations,
                                                    int numberOfProcedures,
                                                    int numberOfMedications) throws ProcessException {
        Map<Process , Integer> mapOfProcesses = new HashMap<>();

        mapOfProcesses.put(findById(OPERATION_ID),numberOfOperations);
        mapOfProcesses.put(findById(PROCEDURE_ID),numberOfProcedures);
        mapOfProcesses.put(findById(MEDICATION_ID),numberOfMedications);

        return mapOfProcesses;
    }
}
