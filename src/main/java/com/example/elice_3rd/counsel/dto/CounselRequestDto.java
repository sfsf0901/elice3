package com.example.elice_3rd.counsel.dto;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor
public class CounselRequestDto {
    @NotBlank
    @Size(min = 3)
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private String category;
}
