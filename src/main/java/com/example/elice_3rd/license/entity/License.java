package com.example.elice_3rd.license.entity;

import com.example.elice_3rd.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "license")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class License {

    @Id
    @Column(name = "member_id")
    private Long memberId; 

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "status", nullable = false)
    private Boolean status = false; // 기본적으로 미인증 상태

    @Column(name = "business_registration", unique = true, nullable = false)
    private String businessRegistration; // 의사면허번호
}
