package com.example.elice_3rd.member.service;

import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class MemberService {
    private final MemberManagementService managementService;
    private final MemberRetrieveService retrieveService;

    public void register(MemberRequestDto requestDto){
        managementService.register(requestDto);
    }

    public MemberResponseDto retrieveMember(String email){
        return retrieveService.retrieve(email);
    }

    public void updatePassword(String email, String password){
        managementService.updatePassword(email, password);
    }

    public void updateMemberInfo(String email, MemberUpdateDto updateDto){
        managementService.updateInfo(email, updateDto);
    }

    public void quit(String email){
        managementService.quit(email);
    }
}
