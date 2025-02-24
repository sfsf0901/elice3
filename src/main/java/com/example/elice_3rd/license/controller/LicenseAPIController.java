package com.example.elice_3rd.license.controller;

import com.example.elice_3rd.license.util.AES256Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/licenses")
public class LicenseAPIController {
    private final AES256Util aes256Util;

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody String JUMIN){
        return ResponseEntity.ok(aes256Util.encrypt(JUMIN));
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decrypt(@RequestBody String JUMIN){
        return ResponseEntity.ok(aes256Util.decrypt(JUMIN));
    }
}
