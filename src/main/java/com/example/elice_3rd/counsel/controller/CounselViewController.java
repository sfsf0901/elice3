package com.example.elice_3rd.counsel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/counsels")
public class CounselViewController {
    @GetMapping
    public String list(){
        return "counsel/list";
    }

    @GetMapping("/{id}")
    public String detail() {
        return "counsel/detail";
    }

    @GetMapping("write")
    public String write() {
        return "counsel/write";
    }
}
