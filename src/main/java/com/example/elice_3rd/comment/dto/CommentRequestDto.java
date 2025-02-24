package com.example.elice_3rd.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentRequestDto {
    @NotNull
    private Long counselId;
    @NotBlank
    @Size(min = 2, max = 500)
    private String content;
}
