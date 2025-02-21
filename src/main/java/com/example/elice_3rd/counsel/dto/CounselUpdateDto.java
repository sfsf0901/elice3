package com.example.elice_3rd.counsel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class CounselUpdateDto extends CounselRequestDto{
    @NotNull
    private Long counselId;
}
