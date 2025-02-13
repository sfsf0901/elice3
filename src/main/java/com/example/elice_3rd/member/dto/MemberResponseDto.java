package com.example.elice_3rd.member.dto;

import com.example.elice_3rd.member.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDto {
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String contact;
    @NotNull
    private Role role;
}
