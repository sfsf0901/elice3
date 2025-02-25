package com.example.elice_3rd.counsel.controller;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.category.service.CategoryService;
import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import com.example.elice_3rd.counsel.dto.CounselUpdateDto;
import com.example.elice_3rd.counsel.service.CounselService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/counsels")
@Validated
public class CounselAPIController {
    private final CounselService counselService;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> write(Principal principal, @Validated @RequestBody CounselRequestDto requestDto){
        counselService.create(principal.getName(), requestDto);
        return ResponseEntity.created(URI.create("/counsels")).build();
    }

    @PatchMapping
    public ResponseEntity<Void> update(Principal principal, @Validated @RequestBody CounselUpdateDto updateDto) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(updateDto));
        counselService.update(principal.getName(), updateDto);
        return ResponseEntity.ok().location(URI.create("/counsels/" + updateDto.getCounselId())).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(Principal principal, Long id){
        counselService.delete(principal.getName(), id);
        return ResponseEntity.noContent().location(URI.create("/counsels")).build();
    }

    @GetMapping("detail")
    public ResponseEntity<CounselResponseDto> retrieveDetail(Long id){
        return ResponseEntity.ok(counselService.retrieveDetail(id));
    }

    @GetMapping
    public ResponseEntity<Page<CounselResponseDto>> retrieveAll(@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                                                                @RequestParam(defaultValue = "") String keyword){
        log.warn("keyword : {}", keyword);
        return ResponseEntity.ok(counselService.retrieveAll(keyword, pageable));
    }

    @GetMapping("/my-counsels")
    public ResponseEntity<Page<CounselResponseDto>> retrieveMyCounsels(Principal principal,
                                                                       @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(counselService.retrieveMyCounsels(principal.getName(), pageable));
    }

    // TODO SRP 위배 진료과 컨트롤러 따로 구현하기
    @GetMapping("/category")
    public ResponseEntity<List<Category>> retrieveAllCategory(){
        return ResponseEntity.ok(categoryService.findAll());
    }
}
