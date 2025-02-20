package com.example.elice_3rd.comment.service;

import com.example.elice_3rd.comment.dto.CommentResponseDto;
import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.comment.repository.CommentRepository;
import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.repository.CounselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentRetrieveService {
    private final CommentRepository commentRepository;
    private final CounselRepository counselRepository;

    public Page<CommentResponseDto> retrieveAll(Long counselId, Pageable pageable){
        // TODO exception 처리
        Counsel counsel = counselRepository.findById(counselId).orElseThrow(() ->
                new NoSuchDataException("답변 조회 실패: 상담 아이디와 일치하는 상담 게시글이 존재하지 않습니다."));
        return commentRepository.findAllByCounsel(counsel, pageable).map(Comment::toDto);
    }

    public Boolean isExist(Long counselId){
        Counsel counsel = counselRepository.findById(counselId).orElseThrow(() ->
                new NoSuchDataException("답변 조회 실패: 상담 아이디와 일치하는 상담 게시글이 존재하지 않습니다."));
        return !commentRepository.findAllByCounsel(counsel, PageRequest.of(0, 1)).isEmpty();
    }
}
