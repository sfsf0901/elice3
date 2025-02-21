package com.example.elice_3rd.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/members")
public class MemberViewController {
    @GetMapping("/register")
    public String register(){
        return "member/user-register";
    }

    @GetMapping("/login")
    public String login(){
        return "/member/another-login";
    }

    @GetMapping("/my-page")
    public String myPage() {
        return "/member/my-page";
    }

    @GetMapping("/")
    public String test(){
        return "/member/temp-page";
    }

    @GetMapping("/my-page/counsels")
    public String myCounsels() {
        return "/member/my-counsels";
    }

    @GetMapping("/verify")
    public String verify(){
        return "/member/verify";
    }
}
