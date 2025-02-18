package com.example.elice_3rd.license.service;

import com.example.elice_3rd.license.entity.License;
import com.example.elice_3rd.license.repository.LicenseRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final MemberRepository memberRepository;

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow();
    }

    public void createLicense(Principal principal, String licenseNumber) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(
                () -> new IllegalArgumentException("create license entity failed: member does not exist")
        );
        License license = License.builder()
                .member(member)
                .licenseNumber(licenseNumber)
                .build();
        licenseRepository.save(license);
    }

    public License getLicense(String email) {
        return licenseRepository.findByMember(getMemberByEmail(email)).orElseThrow(
                () -> new IllegalArgumentException("retrieve license failed: license does not exist")
        );
    }

    public void delete(String email) {
        License license = licenseRepository.findByMember(getMemberByEmail(email)).orElseThrow();
        licenseRepository.delete(license);
    }
}
