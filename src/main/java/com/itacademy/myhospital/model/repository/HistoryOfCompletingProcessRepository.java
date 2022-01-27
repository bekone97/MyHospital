package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.HistoryOfCompletingProcess;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface  HistoryOfCompletingProcessRepository extends JpaRepository<HistoryOfCompletingProcess,Integer> {

    public List<HistoryOfCompletingProcess> findByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);


    public void removeHistoryOfCompletingProcessesByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);

    @Query(value = "SELECT * FROM history_of_completing_processes c " +
            "JOIN medical_history_processes p " +
            "ON (c.medical_history_process_id=p.id)" +
            "where p.medical_history_id=(?1)",nativeQuery = true)
    public List<HistoryOfCompletingProcess> findByMedicalHistoryId(Integer medicalHistoryId);

    boolean existsByMedicalHistoryProcess(MedicalHistoryProcess medicalHistoryProcess);



}
