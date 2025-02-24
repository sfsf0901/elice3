package com.example.elice_3rd.member.service;

import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.dto.PasswordDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberManagementService managementService;
    private final MemberRetrieveService retrieveService;

    public void register(MemberRequestDto requestDto){
        managementService.register(requestDto);
    }

    public MemberResponseDto retrieveMember(String email){
        return retrieveService.retrieve(email);
    }

    public void updatePassword(String email, PasswordDto passwordDto){
        managementService.updatePassword(email, passwordDto);
    }

    public void updateMemberInfo(String email, MemberUpdateDto updateDto){
        managementService.updateInfo(email, updateDto);
    }

    public void quit(String email, String password){
        managementService.quit(email, password);
    }

    public void updateRole(String email){
        managementService.updateRole(email);
    }
}
