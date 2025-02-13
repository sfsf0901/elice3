package com.example.elice_3rd.license.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.elice_3rd.license.entity.License;
import com.example.elice_3rd.license.repository.LicenseRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.entity.Role;
import com.example.elice_3rd.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void registerLicense(Long memberId, String businessRegistration) {
    	Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!member.getRole().equals(Role.DOCTOR)) {
            throw new IllegalArgumentException("의사만 면허번호를 등록할 수 있습니다.");
        }

        if (licenseRepository.findByBusinessRegistration(businessRegistration).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 면허번호입니다.");
        }

        License license = License.builder()
                .member(member)
                .status(false) // 기본적으로 미인증 상태
                .businessRegistration(businessRegistration)
                .build();

        licenseRepository.save(license);
    }

    @Transactional
    public void verifyLicense(Long memberId) {
        License license = licenseRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("의사 면허 정보를 찾을 수 없습니다."));
        
        license.setStatus(true); // 인증 완료
        licenseRepository.save(license);
    }
}
