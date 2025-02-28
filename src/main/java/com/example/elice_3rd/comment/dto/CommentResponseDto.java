package com.example.elice_3rd.comment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentResponseDto {
    @NotNull
    private Long counselId;
    @NotBlank
    private String title;
    @NotBlank
    private String category;
    @NotBlank
    private String content;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private Long memberId;
    @NotNull
    private LocalDateTime createdDate;
}
