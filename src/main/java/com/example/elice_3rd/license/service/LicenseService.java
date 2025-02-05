package com.example.elice_3rd.license.service;

import com.example.elice_3rd.license.entity.License;
import com.example.elice_3rd.license.repository.LicenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;

    public void createLicense(License license){
        licenseRepository.save(license);
    }

    // TODO entity 존재하지 않을 때 예외 처리
    public License getLicense(String email){
        return licenseRepository.findByEmail(email).orElseThrow();
    }

    public void delete(String email){
        License license = licenseRepository.findByEmail(email).orElseThrow();
        licenseRepository.delete(license);
    }
}
