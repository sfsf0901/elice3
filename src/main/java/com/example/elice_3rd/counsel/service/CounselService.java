package com.example.elice_3rd.counsel.service;

import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounselService {
    private final CounselManagementService managementService;
    private final CounselRetrieveService retrieveService;

    public void create(String email, CounselRequestDto requestDto){
        managementService.create(email, requestDto);
    }

    public void update(String email, Long id, CounselRequestDto requestDto){
        managementService.update(email, id, requestDto);
    }

    public void delete(String email, Long id){
        managementService.delete(email, id);
    }

    // 기본 정렬 최신순
    public Page<CounselResponseDto> retrieveAll(Pageable pageable){
        return retrieveService.retrieveAll(pageable);
    }

    public CounselResponseDto retrieveDetail(Long id){
        return retrieveService.retrieveDetail(id);
    }

    public Page<CounselResponseDto> retrieveMyCounsels(String email, Pageable pageable) {
        return retrieveService.retrieveMyCounsels(email, pageable);
    }
}
