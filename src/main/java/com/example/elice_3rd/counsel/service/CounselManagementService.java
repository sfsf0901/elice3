package com.example.elice_3rd.counsel.service;

import com.example.elice_3rd.comment.repository.CommentRepository;
import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.repository.CounselRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CounselManagementService {
    private final CounselRepository counselRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    private Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new NoSuchDataException("이메일과 일치하는 회원이 없습니다.")
        );
    }

    public void create(String email, CounselRequestDto requestDto) {
        Member member = findByEmail(email);
        counselRepository.save(Counsel.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .member(member)
                .build());
    }

    @Transactional
    public void update(String email, Long id, CounselRequestDto requestDto) {
        Counsel counsel = counselRepository.findById(id).orElseThrow(
                () -> new NoSuchDataException("수정 실패: 요청 파라미터와 일치하는 상담 게시글이 없습니다.")
        );
        if(!counsel.isAuthorMatched(email))
            throw new IllegalArgumentException("상담 게시글의 작성자가 일치하지 않습니다.");
        if(commentRepository.findByCounsel(counsel).isPresent())
            throw new IllegalArgumentException("상담에 대한 답변이 존재할 경우 게시글 수정 및 삭제가 불가합니다.");

        counsel.update(requestDto);
    }

    @Transactional
    public void delete(String email, Long id){
        Counsel counsel = counselRepository.findById(id).orElseThrow(
                () -> new NoSuchDataException("삭제 실패: 요청 파라미터와 일치하는 상담 게시글이 없습니다.")
        );
        if(!counsel.isAuthorMatched(email))
            throw new IllegalArgumentException("상담 게시글의 작성자가 일치하지 않습니다.");
        if(commentRepository.findByCounsel(counsel).isPresent())
            throw new IllegalArgumentException("상담에 대한 답변이 존재할 경우 게시글 수정 및 삭제가 불가합니다.");

        counselRepository.delete(counsel);
    }


}
