package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryProcessRepository extends JpaRepository<MedicalHistoryProcess, Integer> {
}
