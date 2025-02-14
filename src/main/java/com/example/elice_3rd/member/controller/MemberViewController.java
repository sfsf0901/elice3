package com.example.elice_3rd.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
//@RequestMapping("/members")
public class MemberViewController {
    @GetMapping("/register")
    public String register(){
        return "user-register";
    }

    @GetMapping("/doctor/register")
    public String doctorRegister(){
        return "member/doctor-register";
    }

    @GetMapping("/login")
    public String login(){
        return "/member/another-login";
    }

    @GetMapping("/my-page")
    public String myPage() {
        return "/member/my-page";
    }

    @GetMapping("/auth")
    public String auth(){
        return "/member/auth";
    }

    @GetMapping("/")
    public String test(){
        return "/member/temp-page";
    }
}
