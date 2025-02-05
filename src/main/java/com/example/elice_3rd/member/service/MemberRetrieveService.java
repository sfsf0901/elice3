package com.example.elice_3rd.member.service;

import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Slf4j
@Component
@AllArgsConstructor
public class MemberRetrieveService {
    private final MemberRepository memberRepository;

    public MemberResponseDto retrieve(String email){
        log.warn("email : {}", email);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원"));
        return member.toResponseDto();
    }
}
