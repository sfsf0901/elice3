package com.example.elice_3rd.member.entity;

import java.time.LocalDate;
import java.util.List;

import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.license.entity.License;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "member_id")
	    private Long memberId; // 기본 키

	    @Column(name = "email", length = 255, unique = true, nullable = false)
	    private String email;

	    @Column(name = "name", length = 10, nullable = false)
	    private String name;

	    @Column(name = "contact", length = 20)
	    private String contact;

	    @Column(name = "password", nullable = false)
	    private String password;

	    @Column(name = "is_deleted")
	    private Boolean isDeleted = false;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "role", nullable = false)
	    private Role role;
	    
	    @Column(name = "created_date", nullable = false)
	    private LocalDate createdDate;

	    @Column(name = "deleted_date")
	    private LocalDate deletedDate;

	    @Column(name = "hospital_id")
	    private Long hospitalId;
	    
	    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	    private List<Counsel> counsels; 
	    
	    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
	    private License license; 
	
}
