package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.service.UUIDService;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class UUIDServiceImpl implements UUIDService {
    public String getRandomString(){
        return UUID.randomUUID().toString();
    }
}
