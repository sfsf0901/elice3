package com.example.elice_3rd.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentResponseDto {
    private String content;
    private String email;
}
