package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.HistoryOfCompletingProcessException;
import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.repository.RoleRepository;
import com.itacademy.myhospital.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.itacademy.myhospital.constants.Constants.NO_COMPLETING_PROCESS_WITH_ID;

@Service

public class RoleServiceImpl implements RoleService {

    private  RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }



    @Override
    public List<Role> findAll() {
       return roleRepository.findAll();
    }

    @Override
    public Role findById(Integer id) {
        var optional = roleRepository.findById(id);
        Role role = null;
        if (optional.isPresent()){
            role=optional.get();
        }
        return role;
    }

    @Override
    public void saveAndFlush(Role item) {
        roleRepository.saveAndFlush(item);
    }

    @Override
    public void deleteById(Integer id) {
        if (roleRepository.existsById(id))
        roleRepository.deleteById(id);
    }
}
