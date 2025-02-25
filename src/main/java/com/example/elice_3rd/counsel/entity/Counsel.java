package com.example.elice_3rd.counsel.entity;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.counsel.dto.CounselResponseDto;
import com.example.elice_3rd.counsel.dto.CounselUpdateDto;
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
    @Column(nullable = false, length = 30)
    private String title;
    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public void update(CounselUpdateDto updateDto, Category category){
        title = updateDto.getTitle();
        content = updateDto.getContent();
        this.category = category;
    }

    public CounselResponseDto toDto(){
        return CounselResponseDto.builder()
                .counselId(counselId)
                .name(member.getName())
                .email(member.getEmail())
                .title(title)
                .content(content)
                .category(category.getName())
                .createdDate(getCreatedDate())
                .build();
    }

    public Boolean isAuthorMatched(String email){
        return email.equals(member.getEmail());
    }
}
