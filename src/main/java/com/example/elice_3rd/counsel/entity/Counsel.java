package com.example.elice_3rd.counsel.entity;

import java.time.LocalDate;
import java.util.List;

import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "counsel")
@Getter
@Setter
@NoArgsConstructor
public class Counsel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="counsel_id")
	private Long counselId;
	
	@Column(name = "title", length = 50, nullable = false)
    private String title;
	
	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // TEXT, NOT NULL

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate; // DATE, NOT NULL

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate; // DATE, NULL 허용

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; 
    
    @OneToMany(mappedBy = "counsel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}

