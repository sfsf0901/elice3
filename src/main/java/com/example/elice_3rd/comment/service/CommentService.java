package com.example.elice_3rd.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.comment.respository.CommentRepository;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.counsel.repository.CounselRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService { 
    private final CommentRepository commentRepository;
    private final CounselRepository counselRepository;
	// private final MemberRepository memberRepository;
    
    
    public List<Comment> getCommentsByCounselId(Long counselId) {
        return commentRepository.findByCounsel_CounselId(counselId);
    }

    @Transactional
    public Comment addComment(Long counselId, String content) {  
        Counsel counsel = counselRepository.findById(counselId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상담 정보를 찾을 수 없습니다."));
        
        Comment comment = Comment.builder()
                .counsel(counsel)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }
}
