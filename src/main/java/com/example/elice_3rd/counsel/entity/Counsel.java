package com.example.elice_3rd.counsel.entity;

import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.counsel.dto.CounselRequestDto;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import com.example.elice_3rd.member.entity.Member;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@RequiredArgsConstructor
public class Counsel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long counselId;
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void update(CounselRequestDto requestDto){
        title = requestDto.getTitle();
        content = requestDto.getContent();
    }

    public CounselResponseDto toDto(){
        return CounselResponseDto.builder()
                .counselId(counselId)
                .email(member.getEmail())
                .title(title)
                .content(content)
                .createdDate(getCreatedDate())
                .build();
    }

    public Boolean isAuthorMatched(String email){
        return email.equals(member.getEmail());
    }
}
