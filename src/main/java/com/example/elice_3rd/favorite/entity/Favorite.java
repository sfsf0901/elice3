package com.example.elice_3rd.favorite.entity;

import java.time.LocalDate;

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
@Table(name="favorite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT 설정
	    @Column(name = "favorite_id")
	    private Long favoriteId;

	    @Column(name = "hospital_id", nullable = false)
	    private Long hospitalId;

	    @Column(name = "created_date", nullable = false)
	    private LocalDate createdDate;

	    @ManyToOne
	    @JoinColumn(name = "member_id", nullable = false)
	    private Member member;
}
