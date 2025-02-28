package com.example.elice_3rd.comment.service;

import com.example.elice_3rd.comment.dto.CommentRequestDto;
import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.comment.repository.CommentRepository;
import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.repository.CounselRepository;
import com.example.elice_3rd.member.entity.Member;
import com.example.elice_3rd.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentManagementService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final CounselRepository counselRepository;

    public void create(String email, CommentRequestDto requestDto){
        Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                new NoSuchDataException("답변 생성 실패: 로그인한 회원 정보와 일치하는 회원이 없습니다.")
        );
        Counsel counsel = counselRepository.findById(requestDto.getCounselId()).orElseThrow(() ->
                new NoSuchDataException("답변 생성 실패: 답변을 작성하려는 상담 내역이 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .member(member)
                .counsel(counsel)
                .build();

        commentRepository.save(comment);
    }
}
