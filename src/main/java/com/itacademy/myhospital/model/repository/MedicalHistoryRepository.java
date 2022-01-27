package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory,Integer> {
    public List<MedicalHistory> findByReceiptDate(LocalDate receiptDate);
    public List<MedicalHistory> findByDischargeDate(LocalDate receiptDate);
    public List<MedicalHistory> findByPatient(Person patient);
    public MedicalHistory findByPatientAndReceiptDate(Person patient,
                                                      LocalDate receiptDate);

}
