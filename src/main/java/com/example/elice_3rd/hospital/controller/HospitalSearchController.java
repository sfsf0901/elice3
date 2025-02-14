package com.example.elice_3rd.hospital.controller;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.service.CategoryService;
import com.example.elice_3rd.hospital.dto.response.HospitalResponse;
import com.example.elice_3rd.hospital.service.HospitalSearchService;
import com.example.elice_3rd.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HospitalSearchController {

    private final HospitalSearchService hospitalSearchService;
    private final CategoryService categoryService;

    @GetMapping("/main")
    public String mainPage(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "hospital/mainGet";
    }

    @GetMapping("/search/{categoryName}")
    public String searchByCategoryGet(
            @PathVariable String categoryName,
            @RequestParam Long categoryId,  // 필수가 아닌 경우
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

/*        if (latitude == null) latitude = 37.5665; // 서울 위도
        if (longitude == null) longitude = 126.9780; // 서울 경도*/

        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);

        Slice<HospitalResponse> hospitals = hospitalSearchService.findAllByCategoryIdV2(categoryId, latitude, longitude, customPageable);
        model.addAttribute("hospitals", hospitals);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);

        return "hospital/list";
    }

    @GetMapping("/api/search/{categoryName}")
    @ResponseBody
    public Map<String, Object> searchByCategoryAjax(
            @PathVariable String categoryName,
            @RequestParam Long categoryId,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @PageableDefault(size = 20) Pageable pageable) {

        Pageable customPageable = Pageable.ofSize(pageable.getPageSize()).withPage(page);
        Slice<HospitalResponse> hospitals = hospitalSearchService.findAllByCategoryIdV2(categoryId, lat, lng, customPageable);

        Map<String, Object> result = new HashMap<>();
        result.put("hospitals", hospitals.getContent());
        result.put("nextPage", hospitals.hasNext() ? page + 1 : null);

        return result;
    }




    //    @PostMapping("/search/{categoryName}")
    public String searchByCategoryPost(@RequestParam("categoryId") Long categoryId,
                                   @RequestParam("lat") Double latitude,
                                   @RequestParam("lng") Double longitude,
                                   Model model) {
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        return "hospital/list";
    }

}
