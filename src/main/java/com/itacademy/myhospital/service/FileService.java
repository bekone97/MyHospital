package com.itacademy.myhospital.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public void uploadFile(MultipartFile file);
}
