package com.example.elice_3rd.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
//@RequestMapping("/members")
public class MemberViewController {
    @GetMapping("/register")
    public String register(){
        return "/member/register";
    }

    @GetMapping("/login")
    public String login(){
        return "/member/login";
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

    @GetMapping("/my-page/comments")
    public String myComments(){
        return "/member/my-comments";
    }

    @GetMapping("/verify")
    public String verify(){
        return "/member/verify";
    }

    @GetMapping("members/update")
    public String update(){
        return "/member/update";
    }

    @GetMapping("/update-password")
    public String updatePassword(){
        return "/member/update-password";
    }
}
