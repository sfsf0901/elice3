package com.example.elice_3rd.hospital.dto.request;

import lombok.Data;

@Data
public class HospitalSearchByCategoryCondition {

    private Long categoryId;
    private Boolean hasNightClinic;
    private Boolean hasSundayAndHolidayClinic;
    private Double latitude;
    private Double longitude;

}
