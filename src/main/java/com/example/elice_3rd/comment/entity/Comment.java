package com.example.elice_3rd.comment.entity;

import com.example.elice_3rd.comment.dto.CommentResponseDto;
import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.member.entity.Member;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@RequiredArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "counsel_id", nullable = false)
    private Counsel counsel;

    public CommentResponseDto toDto(){
        return CommentResponseDto.builder()
                .counselId(counsel.toDto().getCounselId())
                .title(counsel.toDto().getTitle())
                .category(counsel.toDto().getCategory())
                .content(content)
                .name(member.getName())
                .email(member.getEmail())
                .createdDate(getCreatedDate())
                .build();
    }
}
