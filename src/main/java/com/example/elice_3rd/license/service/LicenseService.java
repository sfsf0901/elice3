package com.example.elice_3rd.license.service;

import com.example.elice_3rd.license.entity.License;
import com.example.elice_3rd.license.repository.LicenseRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final MemberRepository memberRepository;

    private Member getMemberByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow();
    }

    public void createLicense(License license){
        licenseRepository.save(license);
    }

    // TODO entity 존재하지 않을 때 예외 처리
    public License getLicense(String email){
        return licenseRepository.findByMember(getMemberByEmail(email)).orElseThrow();
    }

    public void delete(String email){
        License license = licenseRepository.findByMember(getMemberByEmail(email)).orElseThrow();
        licenseRepository.delete(license);
    }
}
