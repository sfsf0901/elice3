package com.example.elice_3rd.diagnosisSubject.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDepartmentCodeRequest {

    @NotBlank
    private String code;
}
