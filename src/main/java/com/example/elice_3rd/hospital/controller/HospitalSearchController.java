package com.example.elice_3rd.hospital.controller;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.service.CategoryService;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchByCategoryCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchByKeywordCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchCondition;
import com.example.elice_3rd.hospital.dto.request.HospitalSearchWithEmergencyCondition;
import com.example.elice_3rd.hospital.dto.response.HospitalResponse;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.repository.HospitalDiagnosisSubjectRepository;
import com.example.elice_3rd.hospital.service.HospitalSearchService;
import com.example.elice_3rd.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HospitalSearchController {

    private final HospitalSearchService hospitalSearchService;
    private final CategoryService categoryService;
    private final HospitalDiagnosisSubjectRepository hospitalDiagnosisSubjectRepository;
    private final HospitalService hospitalService;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @GetMapping("/main")
    public String mainPage(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "hospital/mainGet";
    }

    @GetMapping("/hospitals/{categoryName}")
    public String searchByCategory(@PathVariable String categoryName,
                                   @ModelAttribute HospitalSearchByCategoryCondition condition,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @PageableDefault(size = 20) Pageable pageable,
                                   Model model) {
        long startTime = System.currentTimeMillis();

        if (condition.getLatitude() == null) condition.setLatitude(37.5665); // 서울 위도
        if (condition.getLongitude() == null) condition.setLongitude(126.9780); // 서울 위도

        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);

        Slice<HospitalResponse> hospitals = null;

        if (condition.getCategoryId() == null) {
            return "hospital/mainGet";
        } else if (condition.getCategoryId() == 7) { // 소아과
            condition.setHospitalName("소아과");
            hospitals = hospitalSearchService.findAllByHospitalName(condition, customPageable);
        } else {
            hospitals = hospitalSearchService.findAllByCategoryId(condition, customPageable);
        }

        model.addAttribute("hospitals", hospitals);
        model.addAttribute("condition", condition);
        model.addAttribute("categoryName", categoryName);

        long endTime = System.currentTimeMillis();
        System.out.println("컨트롤러 처리 전체 시간(ms): " + (endTime - startTime));

        return "hospital/list";
    }

    @GetMapping("/api/hospitals/{categoryName}")
    @ResponseBody
    public Map<String, Object> searchByCategoryAjax(@ModelAttribute HospitalSearchByCategoryCondition condition,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @PageableDefault(size = 20) Pageable pageable,
                                                    Model model) {
        if (condition.getLatitude() == null) condition.setLatitude(37.5665);
        if (condition.getLongitude() == null) condition.setLongitude(126.9780);

        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);

        Slice<HospitalResponse> hospitals = null;

        if (condition.getCategoryId() ==7) { // 소아과
            condition.setHospitalName("소아과");
            hospitals = hospitalSearchService.findAllByHospitalName(condition, customPageable);
        } else {
            hospitals = hospitalSearchService.findAllByCategoryId(condition, customPageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("hospitals", hospitals.getContent());
        result.put("nextPage", hospitals.hasNext() ? page + 1 : null);

        return result;
    }

    @GetMapping("/hospitals/emergency")
    public String searchEmergency(
            @ModelAttribute HospitalSearchWithEmergencyCondition condition,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {
        if (condition.getLatitude() == null) condition.setLatitude(37.5665); // 서울 위도
        if (condition.getLongitude() == null) condition.setLongitude(126.9780); // 서울 위도


        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);

        condition.setHasNightEmergency(true);
        Slice<HospitalResponse> hospitals = hospitalSearchService.findAllWithEmergency(condition, customPageable);

        model.addAttribute("hospitals", hospitals);
        model.addAttribute("latitude", condition.getLatitude());
        model.addAttribute("longitude", condition.getLongitude());

        return "hospital/emergencyList";
    }

    @GetMapping("/api/hospitals/emergency")
    @ResponseBody
    public Map<String, Object> searchEmergencyAjax(@ModelAttribute HospitalSearchWithEmergencyCondition condition,
                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                   @PageableDefault(size = 20) Pageable pageable) {
        if (condition.getLatitude() == null) condition.setLatitude(37.5665);
        if (condition.getLongitude() == null) condition.setLongitude(126.9780);

        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);

        condition.setHasNightEmergency(true);

        Slice<HospitalResponse> hospitals = hospitalSearchService.findAllWithEmergency(condition, customPageable);

        Map<String, Object> result = new HashMap<>();
        result.put("hospitals", hospitals.isEmpty() ? new ArrayList<>() : hospitals.getContent());
        result.put("nextPage", hospitals.hasNext() ? page + 1 : null);

        return result;
    }

    @GetMapping("/hospitals/search")
    public String searchByKeyword(@ModelAttribute HospitalSearchByKeywordCondition condition,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @PageableDefault(size = 20) Pageable pageable,
                                  Model model) {
        long startTime = System.currentTimeMillis();

        if (condition.getLatitude() == null) condition.setLatitude(37.5665); // 서울 위도
        if (condition.getLongitude() == null) condition.setLongitude(126.9780); // 서울 위도

        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);

        Slice<HospitalResponse> hospitals = null;

        if (condition.getKeyword() == null) {
            return "hospital/mainGet";
        } else {
            System.out.println("########condition.getKeyword() = " + condition.getKeyword());
            hospitals = hospitalSearchService.findAllByKeyword(condition, customPageable);
        }

        model.addAttribute("hospitals", hospitals);
        model.addAttribute("condition", condition);

        long endTime = System.currentTimeMillis();
        System.out.println("컨트롤러 처리 전체 시간(ms): " + (endTime - startTime));

        return "hospital/keywordList";
    }

    @GetMapping("/api/hospitals/search")
    @ResponseBody
    public Map<String, Object> searchByKeywordAjax(@ModelAttribute HospitalSearchByKeywordCondition condition,
                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @PageableDefault(size = 20) Pageable pageable) {
        if (condition.getLatitude() == null) condition.setLatitude(37.5665);
        if (condition.getLongitude() == null) condition.setLongitude(126.9780);

        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);

        Slice<HospitalResponse> hospitals = hospitalSearchService.findAllByKeyword(condition, customPageable);

        Map<String, Object> result = new HashMap<>();
        result.put("hospitals", hospitals.isEmpty() ? new ArrayList<>() : hospitals.getContent());
        result.put("nextPage", hospitals.hasNext() ? page + 1 : null);

        return result;
    }

    @GetMapping("/hospital/{hospitalName}")
    public String getHospitalDetails(@RequestParam("ykiho") String ykiho, Model model) {
        Hospital hospital = hospitalService.findByYkiho(ykiho);
        List<String> diagnosisSubjectNames = hospitalDiagnosisSubjectRepository.findDiagnosisSubjectNamesByHospital(hospital);

        model.addAttribute("hospital",hospital);
        model.addAttribute("diagnosisSubjectNames", diagnosisSubjectNames);
        model.addAttribute("kakaoApiKey", kakaoApiKey);

        return "hospital/hospitalDetails";
    }
}
