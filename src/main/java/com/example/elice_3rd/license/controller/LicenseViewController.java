package com.example.elice_3rd.license.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/license")
public class LicenseViewController {
    @GetMapping
    public String registerLicense(){
        return "license/register-license";
    }

    @GetMapping("progress")
    public String progress(){
        return "/license/in-progress";
    }
}
