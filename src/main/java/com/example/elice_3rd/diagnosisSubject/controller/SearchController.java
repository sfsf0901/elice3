package com.example.elice_3rd.diagnosisSubject.controller;

import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.diagnosisSubject.repository.DiagnosisSubjectRepository;
import com.example.elice_3rd.diagnosisSubject.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final DiagnosisSubjectRepository diagnosisSubjectRepository;
    private final SearchService searchService;

    @GetMapping("/test")
    public String test() {
        return "search/test";
    }

    @GetMapping("/search")
    public String search(Model model) {
        List<DiagnosisSubject> departments = diagnosisSubjectRepository.findAll();
        System.out.println("의료 부서 목록: " + departments); // 로그 확인
        model.addAttribute("medicalDepartments", departments);
        return "search/test";
    }

    @GetMapping("/search/department")
    public String searchHospitalsByDepartment(@RequestParam String department,
                                              @RequestParam double lat,
                                              @RequestParam double lng,
                                              @RequestParam(defaultValue = "1") int page,
                                              Model model) {


        return "search/department";
    }
}
