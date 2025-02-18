package com.example.elice_3rd.comment.service;

import com.example.elice_3rd.comment.dto.CommentRequestDto;
import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.comment.repository.CommentRepository;
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

    //TODO 예외처리 GlobalExceptionHandler 이용할지 고려
    public void create(String email, Long id, CommentRequestDto requestDto){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        Counsel counsel = counselRepository.findById(id).orElseThrow();

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .member(member)
                .counsel(counsel)
                .build();

        commentRepository.save(comment);
    }
}
