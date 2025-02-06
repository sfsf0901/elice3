package com.example.elice_3rd.member.entity;

import com.example.elice_3rd.common.BaseEntity;
import com.example.elice_3rd.license.entity.License;
import com.example.elice_3rd.member.dto.MemberResponseDto;
import com.example.elice_3rd.member.dto.MemberUpdateDto;
import com.example.elice_3rd.security.CustomMemberDetails;
import jakarta.persistence.*;
import lombok.Builder;
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
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String contact;
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
    private Long hospitalId;
//    @OneToOne()
//    private License license;

    public MemberResponseDto toResponseDto(){
        return MemberResponseDto.builder()
                .email(email)
                .name(name)
                .contact(contact)
                .role(role)
                .build();
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateInfo(MemberUpdateDto updateDto){
        email = updateDto.getEmail();
        name = updateDto.getName();
        contact = updateDto.getContact();
    }

    public void quit(){
        isDeleted = true;
    }
}
