package com.example.elice_3rd.member.service;

import com.example.elice_3rd.member.dto.MemberRequestDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberManagementService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    Member toEntity(MemberRequestDto requestDto) {
        return Member.builder()
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .contact(requestDto.getContact())
                .build();
    }

    void register(MemberRequestDto requestDto){
        memberRepository.save(toEntity(requestDto));
    }

    // TODO Entity 없을 때 예외 처리
    @Transactional
    void updatePassword(String email, String password){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        member.updatePassword(passwordEncoder.encode(password));
    }

    @Transactional
    void updateInfo(String email, MemberUpdateDto updateDto){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        member.updateInfo(updateDto);
    }

    @Transactional
    void quit(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        member.quit();
    }
}
