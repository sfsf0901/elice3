package com.example.elice_3rd.member.entity;

import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.security.MemberDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@SuperBuilder
@NoArgsConstructor
@DynamicInsert
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, length = 8)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;
    @Column(nullable = false)
    @ColumnDefault("'USER'")
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime deletedDate;
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isOauth;

    public MemberResponseDto toResponseDto(){
        return MemberResponseDto.builder()
                .email(email)
                .name(name)
                .role(role)
                .build();
    }

    public MemberDetail toDetail(){
        return MemberDetail.builder()
                .email(email)
                .password(password)
                .role(role.getKey())
                .isDeleted(isDeleted)
                .build();
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public Member updateInfo(MemberUpdateDto updateDto){
        name = updateDto.getName();

        return this;
    }

    public void quit(){
        isDeleted = true;
    }

    public void updateRoleDoctor(){
        role = Role.DOCTOR;
    }
}
