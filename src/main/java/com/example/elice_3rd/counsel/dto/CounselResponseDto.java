package com.example.elice_3rd.counsel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CounselResponseDto {
    @NotNull
    private Long counselId;
    @NotBlank
    @Size(min = 3)
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String email;
    @NotBlank
    private String category;
    @NotNull
    private LocalDateTime createdDate;
}
