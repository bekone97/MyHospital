package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.HistoryOfCompletingProcessException;
import com.itacademy.myhospital.model.entity.HistoryOfCompletingProcess;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.repository.HistoryOfCompletingProcessRepository;
import com.itacademy.myhospital.service.HistoryOfCompletingProcessService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class HistoryOfCompletingProcessServiceImpl implements HistoryOfCompletingProcessService {

    public static final String NO_COMPLETING_PROCESS_WITH_ID = "There is no completing process with id : ";
    public static final String MEDICAL_HISTORY_PROCESS_COMPLETED_EXCEPTION = "The medical history process is completed";
    private final HistoryOfCompletingProcessRepository historyOfCompletingProcessRepository;

    public HistoryOfCompletingProcessServiceImpl(HistoryOfCompletingProcessRepository historyOfCompletingProcessRepository) {
        this.historyOfCompletingProcessRepository = historyOfCompletingProcessRepository;
    }

    @Override
    public List<HistoryOfCompletingProcess> findAll() {

        return historyOfCompletingProcessRepository.findAll();
    }

    @Override
    public HistoryOfCompletingProcess findById(Integer id) throws HistoryOfCompletingProcessException {
        var optional = historyOfCompletingProcessRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new HistoryOfCompletingProcessException(NO_COMPLETING_PROCESS_WITH_ID + id);
        }
    }

    @Override
    public void saveAndFlush(HistoryOfCompletingProcess item) {
        historyOfCompletingProcessRepository.saveAndFlush(item);
    }

    @Override
    public void deleteById(Integer id) throws HistoryOfCompletingProcessException {
        if (historyOfCompletingProcessRepository.existsById(id)) {
            historyOfCompletingProcessRepository.deleteById(id);
        } else {
            throw new HistoryOfCompletingProcessException(NO_COMPLETING_PROCESS_WITH_ID + id);
        }
    }

    @Override
    public List<HistoryOfCompletingProcess> findByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess) {
        return historyOfCompletingProcessRepository.findByMedicalHistoryProcess(medicalHistoryProcess);
    }

    /**
     * This method checks the MedicalHistoryProcess and creates  a new HistoryOfCompletingProcess with the personal, the result,the date of
     * execution,and with the MedicalHistoryProcess
     * @param medicalHistoryProcess - what process was executed
     * @param person - by whom the process was executed
     * @param result - execution result
     * @return true
     * @throws HistoryOfCompletingProcessException if the status of MedicalHistoryProcess is true or if the number
     * of executed processes equal or more than the number of required
     */
    public boolean checkNumberOfExecutionsAndCreateNewExecution(MedicalHistoryProcess medicalHistoryProcess,
                                                                                   Person person,
                                                                                   String result) throws HistoryOfCompletingProcessException {

       medicalHistoryProcess=checkNumberOfExecutions(medicalHistoryProcess);
        var completingProcess = HistoryOfCompletingProcess.builder()
                .medicalHistoryProcess(medicalHistoryProcess)
                .dateOfCompleting(new Timestamp(System.currentTimeMillis()))
                .personal(person)
                .resultOfCompleting(result)
                .build();
        saveAndFlush(completingProcess);
        return true;
    }

    /**
     * This method checks the number of executed processes with the number of required executions and checks status
     * of the MedicalHistoryProcess
     * @param medicalHistoryProcess - what process was executed
     * @return MedicalHistoryProcess
     * @throws HistoryOfCompletingProcessException if the status of MedicalHistoryProcess is true or if the number
     *      * of executed processes equal or more than the number of required
     */
    private MedicalHistoryProcess checkNumberOfExecutions(MedicalHistoryProcess medicalHistoryProcess) throws HistoryOfCompletingProcessException {
        var listOfCompletingProcess = findByMedicalHistoryProcess(medicalHistoryProcess);
        var numberOfCompletedExecutions = listOfCompletingProcess.size();
        numberOfCompletedExecutions++;
        if (numberOfCompletedExecutions == getNumberOfRequiredCompleting(medicalHistoryProcess)){
            medicalHistoryProcess.setStatus(true);
        }else if (numberOfCompletedExecutions>getNumberOfRequiredCompleting(medicalHistoryProcess)){
                throw new HistoryOfCompletingProcessException(MEDICAL_HISTORY_PROCESS_COMPLETED_EXCEPTION);
            }
        return medicalHistoryProcess;
        }


    private int getNumberOfRequiredCompleting(MedicalHistoryProcess medicalHistoryProcess) {
        int numberOfRequiredExecution = medicalHistoryProcess.getNumberOfDays();
        var quantityPerDay = medicalHistoryProcess.getQuantityPerDay();
        if (quantityPerDay > 1) {
            numberOfRequiredExecution = numberOfRequiredExecution * quantityPerDay;
        }
        return numberOfRequiredExecution;
    }

    @Override
    public boolean removeHistoryOfCompletingProcessesByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess) {
        if (historyOfCompletingProcessRepository.existsByMedicalHistoryProcess(medicalHistoryProcess)) {
            historyOfCompletingProcessRepository.removeHistoryOfCompletingProcessesByMedicalHistoryProcess(medicalHistoryProcess);
        }
        return true;
    }


}
