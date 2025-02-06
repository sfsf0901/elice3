package com.example.elice_3rd.license.entity;

import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.member.entity.Member;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Entity
@SuperBuilder
@NoArgsConstructor
@DynamicInsert
public class License extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @MapsId("memberId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(nullable = false)
    private String licenseNumber;
}
