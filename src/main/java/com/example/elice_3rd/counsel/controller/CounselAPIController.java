package com.example.elice_3rd.counsel.controller;

import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import com.example.elice_3rd.counsel.service.CounselService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/counsels")
public class CounselAPIController {
    private final CounselService counselService;

    @PostMapping
    public ResponseEntity<String> create(Principal principal, CounselRequestDto requestDto){
        // TODO create 이후 URI 상담 목록으로
        return ResponseEntity.created(URI.create("/")).build();
    }

    @GetMapping("detail")
    public ResponseEntity<CounselResponseDto> retrieveDetail(Long id){

    }

    @GetMapping
    public ResponseEntity<Page<CounselResponseDto>> retrieveAll(){

    }
}
