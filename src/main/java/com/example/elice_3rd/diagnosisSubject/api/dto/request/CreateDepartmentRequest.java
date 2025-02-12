package com.example.elice_3rd.diagnosisSubject.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDepartmentRequest {

    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
