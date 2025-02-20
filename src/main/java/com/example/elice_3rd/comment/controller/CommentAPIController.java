package com.example.elice_3rd.comment.controller;

import com.example.elice_3rd.comment.dto.CommentRequestDto;
import com.example.elice_3rd.comment.dto.CommentResponseDto;
import com.example.elice_3rd.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentAPIController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> create(Principal principal, @RequestBody CommentRequestDto requestDto){
        commentService.create(principal.getName(), requestDto);
        return ResponseEntity.created(URI.create("/")).build();
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponseDto>> retrieveAll(Long counselId, @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(commentService.retrieveAll(counselId, pageable));
    }
}
