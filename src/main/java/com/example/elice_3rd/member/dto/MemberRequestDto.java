package com.example.elice_3rd.member.dto;

import com.example.elice_3rd.member.entity.Role;
import lombok.Builder;

@Builder
public class MemberRequestDto {
    private String email;
    private String name;
    private String contact;
    private String password;
    private Boolean isDeleted;
    private Role role;
}
