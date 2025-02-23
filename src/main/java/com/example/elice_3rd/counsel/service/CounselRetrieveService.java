package com.example.elice_3rd.counsel.service;

import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.repository.CounselRepository;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.diagnosisSubject.repository.DiagnosisSubjectRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CounselRetrieveService {
    private final CounselRepository counselRepository;
    private final MemberRepository memberRepository;

    public Page<CounselResponseDto> retrieveAll(String keyword, Pageable pageable){
        Page<Counsel> counselPage = counselRepository.searchAllByKeyword(keyword, pageable);
        return counselPage.map(Counsel::toDto);
    }

    public CounselResponseDto retrieveDetail(Long id){
        Counsel counsel = counselRepository.findById(id).orElseThrow(
                () -> new NoSuchDataException("상담 상세 조회 실패: 요청 파라미터와 일치하는 상담 게시글이 없습니다.")
        );
        return counsel.toDto();
    }

    public Page<CounselResponseDto> retrieveMyCounsels(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new NoSuchDataException("상담 상세 조회 실패: 요청 파라미터와 일치하는 상담 게시글이 없습니다.")
        );
        Page<Counsel> counselPage = counselRepository.findAllByMember(member, pageable);

        return counselPage.map(Counsel::toDto);
    }
}
