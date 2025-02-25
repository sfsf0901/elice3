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
    @Size(min = 2, max = 30)
    private String title;
    @NotBlank
    @Size(min = 2, max = 1000)
    private String content;
    @NotBlank
    private String email;
    @NotBlank
    private String category;
    @NotNull
    private LocalDateTime createdDate;
    @NotBlank
    private String name;
}
