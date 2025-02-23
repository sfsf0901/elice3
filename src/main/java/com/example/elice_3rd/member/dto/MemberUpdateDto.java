package com.example.elice_3rd.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberUpdateDto {
    @NotBlank
    private String name;
}