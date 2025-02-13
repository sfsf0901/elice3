package com.example.elice_3rd.hospital.dto;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalInfo {

    private String ykiho;
    private String yadmNm;
    private String postNo;
    private String addr;
    private String telno;
    private String hospUrl;
    private String YPos;
    private String XPos;

    private DiagnosisSubject diagnosisSubject;
    private Category category;

}
