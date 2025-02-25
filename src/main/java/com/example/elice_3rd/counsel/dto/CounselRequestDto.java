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
    @Size(min = 2, max = 500)
    private String title;
    @NotBlank
    @Size(min = 2, max = 1000)
    private String content;
    @NotBlank
    private String category;
}
