package com.example.elice_3rd.hospital.dto.request;

import lombok.Data;

@Data
public class HospitalSearchCondition {

    private Long categoryId;
    private String keyword;
    private Boolean hasNightEmergency;
    private Boolean hasNightClinic;
    private Boolean hasSundayAndHolidayClinic;
    private Double latitude;
    private Double longitude;

}
