package com.example.elice_3rd.counsel.service;

import com.example.elice_3rd.counsel.repository.CounselRepository;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounselRetrieveService {
    private final CounselRepository counselRepository;
    private final MemberRepository memberRepository;


}
