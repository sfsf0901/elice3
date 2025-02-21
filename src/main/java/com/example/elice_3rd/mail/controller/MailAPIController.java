package com.example.elice_3rd.mail.controller;

import com.example.elice_3rd.mail.dto.MailDto;
import com.example.elice_3rd.mail.service.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class MailAPIController {
    private final MailService mailService;

    @PostMapping
    public ResponseEntity<Void> sendEmail(@RequestBody MailDto mailDto) throws JsonProcessingException {
        mailService.sendEmail(mailDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verification")
    public ResponseEntity<Boolean> verify(String code) {
        return ResponseEntity.ok(mailService.verify(code));
    }

    @GetMapping("/verification-success")
    public ResponseEntity<Boolean> verifySuccess(String code){
        return ResponseEntity.ok(mailService.isVerifySuccess(code));
    }
}
