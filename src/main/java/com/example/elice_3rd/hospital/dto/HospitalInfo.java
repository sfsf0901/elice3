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
    private String clCdNm;
    private String yadmNm;
    private String postNo;
    private String addr;
    private String telno;
    private String hospUrl;
    private String yPos;
    private String xPos;

    private DiagnosisSubject diagnosisSubject;
    private Category category;

    public Double getLatitudeAsDouble() {
        return yPos != null && !yPos.isEmpty() ? Double.parseDouble(yPos) : null;
    }

    public Double getLongitudeAsDouble() {
        return xPos != null && !xPos.isEmpty() ? Double.parseDouble(xPos) : null;
    }
}
