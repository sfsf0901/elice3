package com.example.elice_3rd.counsel.service;

import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounselService {
    private final CounselManagementService managementService;
    private final CounselRetrieveService retrieveService;

    public void create(String email, CounselRequestDto requestDto){
        managementService.create(email, requestDto);
    }

    public void update(Long id, CounselRequestDto requestDto){
        managementService.update(id, requestDto);
    }

    public void delete(Long id){
        managementService.delete(id);
    }
}
