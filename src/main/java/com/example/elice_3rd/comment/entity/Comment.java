package com.example.elice_3rd.comment.entity;

import java.time.LocalDate;

import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT 설정
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

 
    @ManyToOne
    @JoinColumn(name = "counsel_id", nullable = false)
    private Counsel counsel;
    
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member doctor;
	
}
