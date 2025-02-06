package com.example.elice_3rd.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/members")
public class MemberViewController {
    @GetMapping("register")
    public String register(){
        return "member/register";
    }

    @GetMapping("/login")
    public String login(){
        return "/member/login";
    }

    @GetMapping("/doctor")
    public String test(){
        return "/member/temp-page";
    }
}
