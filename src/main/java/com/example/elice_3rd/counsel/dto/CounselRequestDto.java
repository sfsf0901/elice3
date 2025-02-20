package com.example.elice_3rd.counsel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CounselRequestDto {
    @NotBlank
    @Size(min = 3)
    private String title;
    @NotBlank
    private String content;
}
