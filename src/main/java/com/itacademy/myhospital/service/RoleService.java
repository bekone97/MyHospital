package com.itacademy.myhospital.service;


import com.itacademy.myhospital.model.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();
    Role findById(Integer id);
    void saveAndFlush(Role item);
    void deleteById(Integer id);

    Role findByName(String name);
}
