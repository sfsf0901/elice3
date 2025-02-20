package com.example.elice_3rd.counsel.controller;

import com.example.elice_3rd.comment.service.CommentService;
import com.example.elice_3rd.counsel.service.CounselService;
import com.example.elice_3rd.member.service.MemberService;
import com.example.elice_3rd.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/counsels")
public class CounselViewController {
    private final CounselService counselService;
    private final CommentService commentService;

    @GetMapping
    public String list(){
        return "counsel/list";
    }

    @GetMapping("/{counselId}")
    public String detail(Model model, @PathVariable Long counselId) throws JsonProcessingException {
        model.addAttribute("counsel", counselService.retrieveDetail(counselId));
        model.addAttribute("isCommentExist", commentService.isExist(counselId));
        return "counsel/detail";
    }

    @GetMapping("write")
    public String write() {
        return "counsel/write";
    }

    @GetMapping("update/{counselId}")
    public String update() {
        return "counsel/update";
    }
}
