package com.example.elice_3rd.license.controller;

import com.example.elice_3rd.license.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/licenses")
public class LicenseAPIController {
    private final LicenseService licenseService;

    @PostMapping
    public ResponseEntity<?> registerLicense(Principal principal, String licenseNumber){
        licenseService.createLicense(principal, licenseNumber);
        return ResponseEntity.created(URI.create("/my-page")).build();
    }
}
