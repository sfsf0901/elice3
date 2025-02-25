package com.example.elice_3rd.member.dto;

import com.example.elice_3rd.member.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 2, max = 8)
    private String name;
    @NotNull
    private Role role;
}
