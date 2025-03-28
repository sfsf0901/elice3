package com.example.elice_3rd.counsel.service;

import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import com.example.elice_3rd.counsel.dto.CounselUpdateDto;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CounselService {
    private final CounselManagementService managementService;
    private final CounselRetrieveService retrieveService;

    public void create(String email, CounselRequestDto requestDto){
        managementService.create(email, requestDto);
    }

    public void update(String email, CounselUpdateDto updateDto){
        managementService.update(email, updateDto);
    }

    public void delete(String email, Long id){
        managementService.delete(email, id);
    }

    // 기본 정렬 최신순
    public Page<CounselResponseDto> retrieveAll(String keyword, Pageable pageable){
        return retrieveService.retrieveAll(keyword, pageable);
    }

    public CounselResponseDto retrieveDetail(Long id){
        return retrieveService.retrieveDetail(id);
    }

    public Page<CounselResponseDto> retrieveMyCounsels(String email, Pageable pageable) {
        return retrieveService.retrieveMyCounsels(email, pageable);
    }
}
