package com.example.elice_3rd.comment.service;

import com.example.elice_3rd.comment.dto.CommentRequestDto;
import com.example.elice_3rd.comment.dto.CommentResponseDto;
import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentManagementService managementService;
    private final CommentRepository commentRepository;

    public void create(String email, Long id, CommentRequestDto requestDto){
        managementService.create(email, id, requestDto);
    }

    public Page<CommentResponseDto> retrieveAll(Pageable pageable){
        return commentRepository.findAll(pageable).map(Comment::toDto);
    }
}
