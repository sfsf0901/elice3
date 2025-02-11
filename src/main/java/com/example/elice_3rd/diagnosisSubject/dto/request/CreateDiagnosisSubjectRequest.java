package com.example.elice_3rd.diagnosisSubject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDiagnosisSubjectRequest {

    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String categoryName;

    @NotBlank
    private Long CategoryId;
}
