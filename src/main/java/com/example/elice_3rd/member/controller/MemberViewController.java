package com.example.elice_3rd.member.controller;

import com.example.elice_3rd.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@Controller
//@RequestMapping("/members")
public class MemberViewController {
    @GetMapping("/register")
    public String register(Principal principal){
        if(principal != null)
            return "redirect:/";

        return "member/register";
    }

    @GetMapping("/login")
    public String login(Principal principal){
        if(principal != null)
            return "redirect:/";

        return "member/login";
    }

    @GetMapping("/my-page")
    public String myPage(@AuthenticationPrincipal CustomUserDetails member, Model model) {
        return "member/my-page";
    }

    @GetMapping("/my-page/counsels")
    public String myCounsels() {
        return "member/my-counsels";
    }

    @GetMapping("/my-page/comments")
    public String myComments(){
        return "member/my-comments";
    }

    @GetMapping("/verify")
    public String verify(){
        return "member/verify";
    }

    @GetMapping("members/update")
    public String update(){
        return "member/update";
    }

    @GetMapping("/update-password")
    public String updatePassword(){
        return "member/update-password";
    }
}
