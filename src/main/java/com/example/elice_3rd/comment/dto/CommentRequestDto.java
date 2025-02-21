package com.example.elice_3rd.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentRequestDto {
    private Long counselId;
    private String content;
}
