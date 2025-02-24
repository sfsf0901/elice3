package com.example.elice_3rd.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberUpdateDto {
    @NotBlank
    @Size(min = 2, max = 8)
    private String name;
}