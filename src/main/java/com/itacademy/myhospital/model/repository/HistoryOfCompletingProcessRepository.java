package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.HistoryOfCompletingProcess;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface  HistoryOfCompletingProcessRepository extends JpaRepository<HistoryOfCompletingProcess,Integer> {

    List<HistoryOfCompletingProcess> findByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);


    void removeHistoryOfCompletingProcessesByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);

    boolean existsByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);



}
