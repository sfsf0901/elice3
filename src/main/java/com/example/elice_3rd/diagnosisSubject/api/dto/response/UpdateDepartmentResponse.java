package com.example.elice_3rd.diagnosisSubject.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentResponse {
    private String status;
    private String message;
    private Long departmentId;
}
