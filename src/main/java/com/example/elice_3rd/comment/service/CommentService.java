package com.example.elice_3rd.comment.service;

import com.example.elice_3rd.comment.dto.CommentRequestDto;
import com.example.elice_3rd.comment.dto.CommentResponseDto;
import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.comment.repository.CommentRepository;
import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.repository.CounselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentManagementService managementService;
    private final CommentRetrieveService retrieveService;

    public void create(String email,  CommentRequestDto requestDto){
        managementService.create(email, requestDto);
    }

    public Page<CommentResponseDto> retrieveAll(Long counselId, Pageable pageable){
        return retrieveService.retrieveAll(counselId, pageable);
    }
}
