package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.NameOfProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NameOfProcessRepository extends JpaRepository<NameOfProcess,Integer> {
    public List<NameOfProcess> findByName(String name);
}
