package com.example.elice_3rd.member.dto;

import com.example.elice_3rd.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDto {
    private String email;
    private String name;
    private String contact;
    private Role role;
}
